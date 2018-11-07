-- Pre multiply alpha
import Codec.Picture
import System.IO
import System.Environment
import System.Exit
import System.Directory
import System.FilePath
import Control.Monad
import Data.List
import Data.Char

--------------------------------------------------------------------------------
-- Alpha channel manipulation
--------------------------------------------------------------------------------

mulAlpha :: Image PixelRGBA8 -> Image PixelRGBA8
mulAlpha = pixelMap go
  where
  go (PixelRGBA8 r g b a) = PixelRGBA8 (f r) (f g) (f b) a
    where f x = fromIntegral (fromIntegral x * fromIntegral a `div` 255 :: Int)

divAlpha :: Image PixelRGBA8 -> Image PixelRGBA8
divAlpha = pixelMap go
  where
  go (PixelRGBA8 r g b a) = PixelRGBA8 (f r) (f g) (f b) a
    where f x = fromIntegral (min 255 $ fromIntegral x * 255 `div` max 1 (fromIntegral a) :: Int)

tryReadImage :: FilePath -> IO (Image PixelRGBA8)
tryReadImage infile = do
  mimg <- readImage infile
  case mimg of
    Left e -> hPutStrLn stderr e >> exitFailure
    Right img -> return $ convertRGBA8 img

mapPng :: (Image PixelRGBA8 -> Image PixelRGBA8) -> FilePath -> FilePath -> IO ()
mapPng f infile outfile = do
  img <- tryReadImage infile
  let img' = f img
  savePngImage outfile $ ImageRGBA8 img'

blankImage :: Pixel px => Image px
blankImage = generateImage undefined 0 0

cropImage :: Pixel px => (Int,Int) -> (Int,Int) -> Bool -> Image px -> Image px
cropImage (x,y) (w,h) rotate img = generateImage fun w h
  where
  fun dx dy
    | rotate    = pixelAt img (x+dy) (y+w-1-dx)
    | otherwise = pixelAt img (x+dx) (y+dy)

--------------------------------------------------------------------------------
-- Atlas unpacking
--------------------------------------------------------------------------------

type Atlas = [AtlasImage]
data AtlasImage = AtlasImage
  { atlasImage :: FilePath
  , atlasRegions :: [AtlasRegion]
  , atlasSize :: (Int,Int)
  , atlasProps :: [(String,String)]
  }
  deriving (Eq,Show)
data AtlasRegion = AtlasRegion
  { regionName :: String
  , regionRotate :: Bool
  , regionXY, regionSize, regionOrig, regionOffset :: (Int,Int)
  , regionIndex :: Int
  }
  deriving (Eq,Show)

mkAtlasImage :: FilePath -> AtlasImage
mkAtlasImage file = AtlasImage file [] (-1,-1) []

mkAtlasRegion :: FilePath -> AtlasRegion
mkAtlasRegion name = AtlasRegion name False (0,0) (0,0) (0,0) (0,0) (-1)

readAtlasFile :: FilePath -> IO Atlas
readAtlasFile = fmap readAtlas . readFile

readAtlas :: String -> Atlas
readAtlas = readAtlas' . lines

readAtlas' :: [String] -> Atlas
readAtlas' [] = []
readAtlas' (l:ls)
  | ".png" `isSuffixOf` l =
      let (regs, ls') = readAtlasRegions ls
      in regs (mkAtlasImage l) : readAtlas' ls'
  | null l = readAtlas' ls
  | otherwise = error $ "Error parsing atlas: " ++ show l

readAtlasRegions :: [String] -> (AtlasImage -> AtlasImage, [String])
readAtlasRegions [] = (id, [])
readAtlasRegions (l:ls)
  | null l = readAtlasRegions ls
  | ':' `elem` l = readAtlasProperty -- properties of image, ignored
  | ".png" `isSuffixOf` l = (id,l:ls)
  | ':' `notElem` l =
      let (reg,  ls') = readAtlasRegionItems ls
          (regs, ls'') = readAtlasRegions ls'
      in ((\im -> im { atlasRegions = reg (mkAtlasRegion l) : atlasRegions im }) . regs, ls'')
  | otherwise = error $ "Error parsing atlas regions: " ++ show l
  where
  readAtlasProperty
    | k == "size" = ((\im -> im { atlasSize = read $ "(" ++ v ++ ")" }) . reg', ls')
    | otherwise   = ((\im -> im { atlasProps = (k,v) : atlasProps im }) . reg', ls')
    where
    (reg', ls') = readAtlasRegions ls
    (k,v') = break (==':') l
    v = dropWhile isSpace $ drop 1 v'

readAtlasRegionItems :: [String] -> (AtlasRegion -> AtlasRegion, [String])
readAtlasRegionItems [] = (id,[])
readAtlasRegionItems (l:ls)
  | null l          = readAtlasRegionItems ls
  | ':' `notElem` l = (id,l:ls)
  | k == "xy"       = ((\reg -> reg{ regionXY   = read $ "(" ++ v ++ ")" }) . reg', ls')
  | k == "size"     = ((\reg -> reg{ regionSize = read $ "(" ++ v ++ ")" }) . reg', ls')
  | k == "orig"     = ((\reg -> reg{ regionOrig = read $ "(" ++ v ++ ")" }) . reg', ls')
  | k == "offset"   = ((\reg -> reg{ regionOrig = read $ "(" ++ v ++ ")" }) . reg', ls')
  | k == "index"    = ((\reg -> reg{ regionIndex = read v }) . reg', ls')
  | k == "rotate"   = ((\reg -> reg{ regionRotate = v `elem` ["true","True"] }) . reg', ls')
  | otherwise       = readAtlasRegionItems ls
  where
  (reg', ls') = readAtlasRegionItems ls
  l' = dropWhile isSpace l
  (k,v') = break (==':') l'
  v = dropWhile isSpace $ drop 1 v'

writeAtlas :: Atlas -> String
writeAtlas = unlines . concatMap writeAtlasImage

writeAtlasImage :: AtlasImage -> [String]
writeAtlasImage (AtlasImage imFile regions (sizex,sizey) props) =
  [ ""
  , imFile
  , "size: " ++ show sizex ++ "," ++ show sizey
  ]
  ++ [ k ++ ": " ++ v | (k,v) <- props ]
  ++ concatMap writeAtlasRegion regions

writeAtlasRegion :: AtlasRegion -> [String]
writeAtlasRegion (AtlasRegion name rot xy size orig off idx) =
  [ name
  , "  rotate: " ++ showBool rot
  , "  xy: " ++ showTuple xy
  , "  size: " ++ showTuple size
  , "  orig: " ++ showTuple orig
  , "  offset: " ++ showTuple off
  , "  index: " ++ show idx
  ]
  where
  showBool = map toLower . show
  showTuple (a,b) = show a ++ ", " ++ show b

extractAtlas :: (Image PixelRGBA8 -> Image PixelRGBA8) -> Atlas -> IO ()
extractAtlas imFun atlas = do
  mapM_ (extractAtlasImage imFun) atlas

extractAtlasImage :: (Image PixelRGBA8 -> Image PixelRGBA8) -> AtlasImage -> IO ()
extractAtlasImage imFun (AtlasImage imFile regions _ _) = do
  im <- tryReadImage imFile
  let im' = imFun im
  mapM_ (extractAtlasRegion im') regions

extractAtlasRegion :: Image PixelRGBA8 -> AtlasRegion -> IO ()
extractAtlasRegion im region@(AtlasRegion name rot xy size _orig _off _idx) = do
  print region
  let outfile
       | '/' `elem` name = name ++ ".png"
       | otherwise       = "images/" ++ name ++ ".png"
  createDirectoryIfMissing True (takeDirectory outfile)
  savePngImage outfile $ ImageRGBA8 (cropImage xy size rot im)

decodeAtlas :: (Image PixelRGBA8 -> Image PixelRGBA8) -> FilePath -> IO ()
decodeAtlas imFun = extractAtlas imFun <=< readAtlasFile

doubleAtlasCoords :: Atlas -> Atlas
doubleAtlasCoords = map doubleImg
  where
  doubleImg im = im { atlasSize = double (atlasSize im), atlasRegions = map doubleRegion (atlasRegions im) }
  doubleRegion reg = reg { regionSize = double (regionSize reg), regionXY = double (regionXY reg), regionOrig = double (regionOrig reg), regionOffset = double (regionOffset reg) }
  double (x,y) = (2*x, 2*y)

--------------------------------------------------------------------------------
-- Main function
--------------------------------------------------------------------------------

main :: IO ()
main = do
  args <- getArgs
  case args of
    ["mul",infile,outfile] -> mapPng mulAlpha infile outfile
    ["div",infile,outfile] -> mapPng divAlpha infile outfile
    ["atlas",infile] -> decodeAtlas divAlpha infile
    ["atlas-div",infile] -> decodeAtlas divAlpha infile
    ["atlas-nodiv",infile] -> decodeAtlas id infile
    _ -> do
      prog <- getProgName
      hPutStrLn stderr $ "Usage: " ++ prog ++ " mul <INFILE> <OUTFILE>"
      hPutStrLn stderr $ "Multiply by alpha channel."
      hPutStrLn stderr $ ""
      hPutStrLn stderr $ "Usage: " ++ prog ++ " div <INFILE> <OUTFILE>"
      hPutStrLn stderr $ "Divide by alpha channel."
      hPutStrLn stderr $ ""
      hPutStrLn stderr $ "Usage: " ++ prog ++ " atlas <INFILE>"
      hPutStrLn stderr $ "Decode a texture atlas, assuming premultiplied alpha. Writes to images subdirectory"

