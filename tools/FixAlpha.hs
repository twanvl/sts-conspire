-- Pre multiply alpha
import Codec.Picture
import System.IO
import System.Environment
import System.Exit
import System.Directory
import Data.List
import Data.Char

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
    | rotate    = pixelAt img (x+dy) (y+dx)
    | otherwise = pixelAt img (x+dx) (y+dy)

decodeAtlas :: FilePath -> IO ()
decodeAtlas atlasFile = do
  createDirectoryIfMissing False "images"
  atlas <- readFile atlasFile
  go blankImage "" (0,0) (0,0) False (lines atlas)
  where
  go im name xy size rot [] = save im name xy size rot
  go im name xy size rot (l:ls)
    | ".png" `isSuffixOf` l = do
        im' <- tryReadImage l
        let im'' = divAlpha im'
        go im'' name xy size rot ls
    | l' == "rotate: false"               = go im name xy size False ls
    | l' == "rotate: true"                = go im name xy size True ls
    | "size:" `isPrefixOf` l'             = go im name xy (read ("(" ++ drop 5 l' ++ ")")) rot ls
    | "xy:"   `isPrefixOf` l'             = go im name (read ("(" ++ drop 3 l' ++ ")")) size rot ls
    | "images/" `isPrefixOf` l            = save im name xy size rot >> go im (drop 7 l) (0,0) (0,0) False ls
    | l' /= "" && not (":" `isInfixOf` l) = save im name xy size rot >> go im l (0,0) (0,0) False ls
    | otherwise                           = go im name xy size rot ls
    where
    l' = dropWhile isSpace l
  save im name xy size rot
    | null name = return ()
    | otherwise = do
      putStrLn $ "Found: " ++ name ++ " at " ++ show xy ++ " size " ++ show size ++ " rotate? " ++ show rot
      let outfile = "images/" ++ name ++ ".png"
      savePngImage outfile $ ImageRGBA8 (cropImage xy size rot im)
  
  

main :: IO ()
main = do
  args <- getArgs
  case args of
    ["mul",infile,outfile] -> mapPng mulAlpha infile outfile
    ["div",infile,outfile] -> mapPng divAlpha infile outfile
    ["atlas",infile] -> decodeAtlas infile
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

