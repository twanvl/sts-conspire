{-# LANGUAGE OverloadedStrings #-}
{-
A tool to make mesh deformations for the RoseBush animation.
-}

import Data.NumInstances.Tuple
import Data.Aeson as Json
import qualified Data.ByteString.Lazy.Char8 as B

-------------------------------------------------------------------------------
-- Utility
-------------------------------------------------------------------------------

pairList :: (a,a) -> [a]
pairList (x,y) = [x,y]

degToRad :: Double -> Angle
degToRad = (* (pi / 180))

-------------------------------------------------------------------------------
-- Coordinate systems
-------------------------------------------------------------------------------

type Coord  = (Double,Double)
type Vector = (Double,Double)
type Angle  = Double
data Space  = Space { spaceOrigin :: Coord, spaceAngle :: Angle }
  deriving Show

rotate :: Double -> Vector -> Vector
rotate a (x,y) = (cos a * x - sin a * y, sin a * x + cos a * y)

world :: Space
world = Space (0,0) 0

-- local to world
worldVec :: Space -> Vector -> Vector
worldVec s = rotate (spaceAngle s)

worldCoord :: Space -> Coord -> Coord
worldCoord s c = spaceOrigin s + worldVec s c 

worldAngle :: Space -> Angle -> Angle
worldAngle s a = spaceAngle s + a

worldSpace :: Space -> Space -> Space
worldSpace s (Space o a) = Space (worldCoord s o) (worldAngle s a)

-- relative-to-eachother spaces to relative-to-world space
worldSpaces :: [Space] -> [Space]
worldSpaces = scanl worldSpace world


-------------------------------------------------------------------------------
-- RoseBush
-------------------------------------------------------------------------------
{-
width, height :: Double
width = 300
height = 248

parts :: Int
parts = 6

triangles :: [Int]
triangles = concat [ [2*i,2*i+1,2*i+2]++[2*i+2,2*i+1,2*i+3] | i <- [0..parts-1] ]

uvs :: [Double]
uvs = concat [ [0, y, 1, y] | i <- [0..parts], let y = 1 - realToFrac i / realToFrac parts ]

hull :: Int
hull = 2*(parts+1)

-- edges are indices into vertex coord array it seems
edges :: [Int]

edges = map (2*) $ concat $
  [ [2*i,2*i+1]   | i <- [0..parts] ] ++
  [ [2*i,2*i+2]   | i <- [0..parts-1] ] ++
  [ [2*i+1,2*i+3] | i <- [0..parts-1] ]

boneSpace :: Angle -> Double -> Space
boneSpace a l = Space (rotate a (0,l)) a

bones :: Angle -> [Space]
bones angle = replicate parts (boneSpace angle len)
  where len = realToFrac height / realToFrac parts

verticesIn :: Space -> [Double]
verticesIn s = concatMap pairList [worldCoord s (-150,0), worldCoord s (150,0)]

vertices :: Angle -> [Double]
vertices = concatMap verticesIn . worldSpaces . bones
-}

-------------------------------------------------------------------------------
-- General meshes
-------------------------------------------------------------------------------

-- anchor point, relative to mesh size
type Anchor = (Double,Double)
anchorBottom, anchorLeft :: Anchor
anchorBottom = (0.5,0)
anchorLeft = (0,0.5)

data Mesh = Mesh
  { meshWidth, meshHeight :: Double
  , meshPartsX, meshPartsY :: Int
  , meshAnchor :: Anchor
  }

{-
mesh vertices:

8  9 10 11
4  5  6  7
0  1  2  3
-}

-- Texture coordinates for all mesh vertices
-- returned as list [u0,v0, u1,v1, ...]
--
-- Note: v points down instead of up, so v=1 is the bottom
meshUVs :: Mesh -> [Double]
meshUVs (Mesh _ _ px py _) = concat
  [ [realToFrac x / realToFrac px, 1 - realToFrac y / realToFrac py]
  | y <- [0..py]
  , x <- [0..px]
  ]

flipUVs :: [Double] -> [Double]
flipUVs (u:v:uvs) = u:(1-v):flipUVs uvs
flipUVs _ = []

-- number of points in the hull
meshHull :: Mesh -> Int
meshHull m = (meshPartsX m + meshPartsY m) * 2

-- Triangulation of the mesh
-- returned as indices into vertex array
meshTriangles :: Mesh -> [Int]
meshTriangles (Mesh _ _ px py _) = concat
  [ [vertex (x,y), vertex (x+1,y), vertex (x,y+1)
    ,vertex (x,y+1), vertex (x+1,y), vertex (x+1,y+1)]
  | y <- [0..py-1]
  , x <- [0..px-1]
  ]
  where
  vertex (x,y) = x + y * (px+1)

-- Edges of the mesh
-- returned as indices into vertex array
-- In spine, edges are indices into vertex coord array it seems, so *2
meshEdges :: Mesh -> [Int]
meshEdges (Mesh _ _ px py _) = map (2*) $ concat $
  [ [vertex (x,y), vertex (x+1,y)] | y <- [0..py] , x <- [0..px-1] ] ++
  [ [vertex (x,y), vertex (x,y+1)] | y <- [0..py-1] , x <- [0..px] ]
  where
  vertex (x,y) = x + y * (px+1)

-- Vertex coordinates of the mesh, given a coordinate transformation
meshVerticesWith :: (Coord -> Coord) -> Mesh -> [Double]
meshVerticesWith f (Mesh w h px py (ax,ay)) = concat $
  [ pairList $ f ((realToFrac x / realToFrac px - ax) * w, (realToFrac y / realToFrac py - ay) * h)
  | y <- [0..py]
  , x <- [0..px]
  ]

-- Vertices of the mesh, without deformation
meshVertices :: Mesh -> [Double]
meshVertices = meshVerticesWith id

data Deformation
  = Bend { bendX, bendY :: Angle }

type AnglePerUnitLength = Double
-- Coordinates on (or perpendicular to) a circular curve
-- A curve which rotates angle da*y for y moved corresponds to a circle with circumference 2*pi/da.
-- So with radius 1/da.
deformY :: AnglePerUnitLength -> (Coord -> Coord)
deformY 0 (x,y) = (x,y)
deformY da (x,y) = (x0,y0) + rotate angle (x,0)
  where
  angle = da * y
  absAngle = abs da * y
  x0 = (cos absAngle - 1) / da
       -- ^ note: divide by possibly negative da, so the origin can be either to the left or to the right
  y0 = sin absAngle / abs da

deformX :: AnglePerUnitLength -> (Coord -> Coord)
deformX da = rotate (-pi/2) . deformY da . rotate (pi/2)

{-
Note: piecewise rotation with angle a every l lengths corresponds to a continuous deformation
 with radius r = 1/cos ((pi - a)/2) * l/2
-}

-- Vertices of the mesh, after deformation
meshDeltaVertices :: (Coord -> Coord) -> Mesh -> [Double]
meshDeltaVertices f = meshVerticesWith (\c -> f c - c)

instance ToJSON Mesh where
  toJSON m = object
    [ "x" .= (meshWidth  m * (0.5 - fst (meshAnchor m)))
    , "y" .= (meshHeight m * (0.5 - snd (meshAnchor m)))
    , "width" .= meshWidth m
    , "height" .= meshHeight m
    , "type" .= ("mesh" :: String)
    , "vertices" .= meshVertices m
    , "uvs" .= meshUVs m
    , "triangles" .= meshTriangles m
    , "hull" .= meshHull m
    , "edges" .= meshEdges m
    ]

-------------------------------------------------------------------------------
-- Instances
-------------------------------------------------------------------------------

roseBush :: Mesh
roseBush = Mesh 300 248 1 6 anchorBottom

{-
for angle=1 deg:
 meshDeltaVertices (deformY $ degToRad 1/(248/6)) roseBush
-}

leaf :: Mesh
leaf = Mesh 433 243 6 1 (0.023,0.5)
{-
meshDeltaVertices (deformX $ degToRad (-8e-2)) leaf
meshDeltaVertices (deformX $ degToRad (8e-2)) leaf
-}

vine :: Mesh
vine = Mesh 88 474 1 10 (0.5,1)
{-
meshDeltaVertices (deformY $ degToRad (4e-2)) vine
-}
-------------------------------------------------------------------------------
-- JSON input/output
-------------------------------------------------------------------------------

--Skeleton

