{-
A tool to make mesh deformations for the RoseBush animation
-}

import Data.NumInstances.Tuple

-------------------------------------------------------------------------------
-- Utility
-------------------------------------------------------------------------------

pairList :: (a,a) -> [a]
pairList (x,y) = [x,y]

-------------------------------------------------------------------------------
-- Coordinate systems
-------------------------------------------------------------------------------

type Coord  = (Double,Double)
type Vector = (Double,Double)
type Angle  = Double
data Space = Space { spaceOrigin :: Coord, spaceAngle :: Angle }
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

-------------------------------------------------------------------------------
-- Bones etc.
-------------------------------------------------------------------------------

data Bone = Bone { boneAngle, boneLength :: Double }
  deriving Show

boneSpace :: Bone -> Space
boneSpace (Bone a l) = Space (rotate a (0,l)) a

boneSpaces :: [Bone] -> [Space]
boneSpaces = scanl step world
  where
  step s b = worldSpace s (boneSpace b)

-------------------------------------------------------------------------------
-- RoseBush
-------------------------------------------------------------------------------

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

bones :: Double -> [Bone]
bones angle = replicate parts (Bone angle len)
  where len = realToFrac height / realToFrac parts

verticesIn :: Space -> [Double]
verticesIn s = concatMap pairList [worldCoord s (-150,0), worldCoord s (150,0)]

vertices :: Double -> [Double]
vertices = concatMap verticesIn . boneSpaces . bones

