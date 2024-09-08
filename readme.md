# ImageShrinkFX

ImageShrinkFX is a simple image shrinking tool that allows you to shrink images in a batch.
This tool is built with Kotlin and JavaFX.

## Features

- Shrink images in a batch
- JPG, PNG and GIF formats are supported
- You can select input and output directories
- You can set the compression rate of the output images
- All image files in input directory can be processed at once.
- You can see the file size and compression rate of each file in the log.
- The output file will be named (original)-min.jpg, (original)-min.png, or (original)-min.gif.

## How to use

1. Select the input directory.
2. Select the output directory.
3. Set the compression rate (the default is 0.5).
   - The compression rate is a value between 0 and 1.
   - The closer the value is to 1, the higher the quality of the image. But the file size is larger.
   - The closer the value is to 0, the lower the quality of the image. But the file size is smaller.
4. Click the "Start Compression" button.

## Screenshots

![main-screen-1](/screenshot/main-screen-1.jpg)
![main-screen-2](/screenshot/main-screen-2.jpg)

## License

MIT License

## Author

Name: Shingo Suzuki

Blog: [suzushinlab.com](https://suzushinlab.com/)

Twitter: [@suzushin7](https://twitter.com/suzushin7)

GitHub: [suzushin7](https://github.com/suzushin7)
