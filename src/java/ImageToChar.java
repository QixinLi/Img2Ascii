//import com.sun.image.codec.jpeg.ImageFormatException;
//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.FileImageInputStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author lee
 */
public class ImageToChar {
    static String ascii = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\\\"^`'.";
    
	public static char toChar(int g) {
		if (g <= 25) {
			return '#';
		} else if (g > 25 && g <= 50) {
			return '&';
		} else if (g > 50 && g <= 75) {
			return '$';
		} else if (g > 75 && g <= 100) {
			return '%';
		} else if (g > 100 && g <= 125) {
			return '*';
		} else if (g > 125 && g <= 150) {
			return 'o';
		} else if (g > 150 && g <= 175) {
			return '!';
                } else if (g > 175 && g <= 200) {
			return ';';
                } else if (g > 200 && g <= 225) {
			return '.';
		} else {
			return ' ';
		}
	}
 
	public static void load(String imagePath, String txtPath) throws IOException {
		BufferedImage bi = null;
		File imageFile = new File(imagePath);
		bi = ImageIO.read(imageFile);
		load(bi, txtPath);
	}
 
	public static void load(BufferedImage bi, String txtPath) throws IOException {
		File txtFile = new File(txtPath);
		if (!txtFile.exists()) {
			txtFile.getParentFile().mkdirs();
			txtFile.createNewFile();
		}
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txtFile)));
		int width = bi.getWidth();
		int height = bi.getHeight();
		int minx = bi.getMinX();
		int miny = bi.getMinY();
		System.out.println(width + " " + height);
		for (int i = miny; i < height; i += 8) {
			for (int j = minx; j < width; j += 8) {
				int pixel = bi.getRGB(j, i); // 下面三行代码将一个数字转换为RGB数字
				int red = (pixel & 0xff0000) >> 16;
				int green = (pixel & 0xff00) >> 8;
				int blue = (pixel & 0xff);
				double gray = 0.299 * red + 0.578 * green + 0.114 * blue;
				// char c = ascii.charAt((int) (gray / 255 * ascii.length()));
				char c = toChar((int) gray);
				bufferedWriter.write(c);
			}
			bufferedWriter.newLine();
		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}
 
	public static void loadGif(String imagePath, String outPath) throws IOException {
		File imageFile = new File(imagePath);
		FileImageInputStream in = new FileImageInputStream(imageFile);
		ImageReaderSpi readerSpi = new GIFImageReaderSpi();
		GIFImageReader gifImageReader = new GIFImageReader(readerSpi);
		gifImageReader.setInput(in);
		int num = gifImageReader.getNumImages(true);
		System.out.println(num);
		BufferedImage[] bufferedImages = new BufferedImage[num];
		for (int i = 0; i < num; i++) {
			BufferedImage bi = gifImageReader.read(i);
			bufferedImages[i] = txtToImage(bi, outPath + "out" + i + ".jpeg");
		}
		jpgToGif(bufferedImages, outPath , 100);
                for (int i = 0; i < num; i++) {
                    deleteFile(outPath + "out" + i + ".jpeg");
		}
	}
        public static void loadJpeg(String imagePath, String outPath) throws IOException{
            BufferedImage bi = ImageIO.read(new File(imagePath));
            txtToImage(bi, outPath );
        }
        static {
            ImageIO.scanForPlugins();
        }
	public static BufferedImage txtToImage(BufferedImage bi, String outPutPath) {
		File imageFile = new File(outPutPath);
		if (!imageFile.exists()) {
			try {
				imageFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 
		int width = bi.getWidth();
		int height = bi.getHeight();
		int minx = bi.getMinX();
		int miny = bi.getMinY();
		//System.out.println(width + " " + height);
		int speed = 7;
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 获取图像上下文
		Graphics g = createGraphics(bufferedImage, width, height, speed);
		// 图片中文本行高
		final int Y_LINEHEIGHT = speed;
		int lineNum = 1;
		for (int i = miny; i < height; i += speed) {
			// StringBuilder stringBuilder = new StringBuilder();
			for (int j = minx; j < width; j += speed) {
				int pixel = bi.getRGB(j, i); // 下面三行代码将一个数字转换为RGB数字
				int red = (pixel & 0xff0000) >> 16;
				int green = (pixel & 0xff00) >> 8;
				int blue = (pixel & 0xff);
				double gray = 0.299 * red + 0.578 * green + 0.114 * blue;
				// char c = ascii.charAt((int) (gray / 255 * ascii.length()));
				char c = toChar((int) gray);
				// stringBuilder.append(c);
				g.drawString(String.valueOf(c), j, i);
			}
			// g.drawString(stringBuilder.toString(), 0, lineNum * Y_LINEHEIGHT);
			lineNum++;
		}
		g.dispose();
		// 保存为jpg图片
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(imageFile);
                        ImageIO.write(bufferedImage, "jpg", fos);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return bufferedImage;
 
	}
 
	private static void jpgToGif(BufferedImage[] bufferedImages, String newPic, int playTime) {
		try {
			AnimatedGifEncoder e = new AnimatedGifEncoder();
			e.setRepeat(0);
			e.start(newPic);
			for (int i = 0; i < bufferedImages.length; i++) {
				e.setDelay(playTime); // 设置播放的延迟时间
				e.addFrame(bufferedImages[i]); // 添加到帧中
			}
			e.finish();
		} catch (Exception e) {
			System.out.println("jpgToGif Failed:");
		}
	}
 
	private static Graphics createGraphics(BufferedImage image, int width, int height, int size) {
		Graphics g = image.createGraphics();
		g.setColor(null); // 设置背景色
		g.fillRect(0, 0, width, height);// 绘制背景
		g.setColor(Color.BLACK); // 设置前景色
		g.setFont(new Font("微软雅黑", Font.PLAIN, size)); // 设置字体
		return g;
	}
        
         public static boolean deleteFile(String fileName) {
            File file = new File(fileName);
            // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                //System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

 
	public static void Gif2Ascii(String inputname,String outputname) throws IOException {
		// load("C:\\Users\\lenovo\\Desktop\\2.jpg", "f:/charImage/1.txt");
	    loadGif(STATIC.dir+"uploadimg//"+inputname, STATIC.dir+"asciiimg//"+outputname);
		// loadGif("F:\\charImage\\1.gif", "f:/gif/");
	}
        public static void Jpeg2Ascii(String inputname,String outputname) throws IOException{
            loadJpeg(STATIC.dir+"uploadimg//"+inputname, STATIC.dir+"asciiimg//"+outputname);
        }

}