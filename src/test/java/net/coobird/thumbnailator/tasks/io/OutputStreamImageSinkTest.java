package net.coobird.thumbnailator.tasks.io;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import net.coobird.thumbnailator.tasks.UnsupportedFormatException;

import org.junit.jupiter.api.DisplayName;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import javax.imageio.ImageWriter;

public class OutputStreamImageSinkTest {
    private ByteArrayOutputStream outputStream;
    private OutputStreamImageSink sink;
    private BufferedImage testImage;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        sink = new OutputStreamImageSink(outputStream);
        testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    }

    //Testing the Constructor with null
    @Test
    void testConstructorWithNull() {
        assertThrows(NullPointerException.class,
                () -> new OutputStreamImageSink(null),
                "OutputStream cannot be null"
        );
    }

    //Testing the Constructor with valid input OutputStream
    @Test
    void testConstructorWithValidStream() {
        assertDoesNotThrow(() -> new OutputStreamImageSink(new ByteArrayOutputStream()));
    }

    // Jpeg and Bmp Image Format Tests
    @Test
    void testJpegBmpFormatDetection() throws Exception {
        //Using reflection we can test private methods
        Method isJpegOrBmpMethod = OutputStreamImageSink.class.getDeclaredMethod("isJpegOrBmp", String.class);
        isJpegOrBmpMethod.setAccessible(true);

        // Valid inputs, including case sensitive testing.
        assertTrue((Boolean) isJpegOrBmpMethod.invoke(sink, "jpg"));
        assertTrue((Boolean) isJpegOrBmpMethod.invoke(sink, "jpeg"));
        assertTrue((Boolean) isJpegOrBmpMethod.invoke(sink, "bmp"));
        assertTrue((Boolean) isJpegOrBmpMethod.invoke(sink, "JPG"));
        assertTrue((Boolean) isJpegOrBmpMethod.invoke(sink, "JPEG"));
        assertTrue((Boolean) isJpegOrBmpMethod.invoke(sink, "BMP"));

        // Some Invalid Inputs
        assertFalse((Boolean) isJpegOrBmpMethod.invoke(sink, "png"));
        assertFalse((Boolean) isJpegOrBmpMethod.invoke(sink, "gif"));
    }

    // Png Image format tests
    @Test
    void testPngFormatDetection() throws Exception {
        //using reflection we can test private methods
        Method isPngMethod = OutputStreamImageSink.class.getDeclaredMethod("isPng", String.class);
        isPngMethod.setAccessible(true);

        //Valid Inputs, including case sensitive testing
        assertTrue((Boolean) isPngMethod.invoke(sink, "png"));
        assertTrue((Boolean) isPngMethod.invoke(sink, "PNG"));

        //Some Invalid inputs.
        assertFalse((Boolean) isPngMethod.invoke(sink, "jpg"));
        assertFalse((Boolean) isPngMethod.invoke(sink, "jpeg"));
        assertFalse((Boolean) isPngMethod.invoke(sink, "bmp"));
    }

    // Testing Java Version when null
    @Test
    void testJavaVersionNull() throws Exception {
        //Using reflection we can test private methods
        Method isJava9OrNewerMethod = OutputStreamImageSink.class.getDeclaredMethod("isJava9OrNewer");
        isJava9OrNewerMethod.setAccessible(true);

        //Temporary holder for original property
        String original = System.getProperty("java.specification.version");

        //Clear the property to set it to Null
        System.clearProperty("java.specification.version");

        //Testing Null, should return false
        assertFalse((Boolean) isJava9OrNewerMethod.invoke(sink));

        //Post test: Set the java version property back to its original
        System.setProperty("java.specification.version", original);

    }

    // Testing Java version when java 8
    @Test
    void testJavaVersionJava8() throws Exception {
        //Using reflection we can test private methods
        Method isJava9OrNewerMethod = OutputStreamImageSink.class.getDeclaredMethod("isJava9OrNewer");
        isJava9OrNewerMethod.setAccessible(true);

        //Temporary holder for original property
        String original = System.getProperty("java.specification.version");

        //Set property to java 8 (Defined as 1.8)
        System.setProperty("java.specification.version", "1.8");

        //Testing java 8, should return false
        assertFalse((Boolean) isJava9OrNewerMethod.invoke(sink));

        //Post test: Set the java version property back to its original
        System.setProperty("java.specification.version", original);

    }

    // Testing Java version when java 9
    @Test
    void testJavaVersionJava9() throws Exception {
        //Using reflection we can test private methods
        Method isJava9OrNewerMethod = OutputStreamImageSink.class.getDeclaredMethod("isJava9OrNewer");
        isJava9OrNewerMethod.setAccessible(true);

        //Temporary holder for original property
        String original = System.getProperty("java.specification.version");

        //Set property to java 9 (Defined as 9)
        System.setProperty("java.specification.version", "9");

        //Testing java 9, should return True
        assertTrue((Boolean) isJava9OrNewerMethod.invoke(sink));

        //Post test: Set the java version property back to its original
        System.setProperty("java.specification.version", original);

    }


    // 4. Output Stream Access Test
    @Test
    void testGetSink() {
        assertEquals(outputStream, sink.getSink());
    }

    // 5. Write Method Tests
    @Test
    void testWriteWithNullImage() {
        assertThrows(NullPointerException.class,
                () -> sink.write(null),
                "Cannot write a null image"
        );
    }

    // Testing format handling through public write method
    @Test
    void testWriteWithDifferentFormats() {
        String[] formats = {"jpg", "png", "bmp"};
        for (String format : formats) {
            ByteArrayOutputStream newStream = new ByteArrayOutputStream();
            OutputStreamImageSink newSink = new OutputStreamImageSink(newStream);
            newSink.setOutputFormatName(format);

            assertDoesNotThrow(() -> newSink.write(testImage));
            assertTrue(newStream.size() > 0,
                    "Should successfully write image in " + format + " format");
        }
    }

    // Additional tests remain the same...
    
//    UnsupportedFormatException
    
    // Testing format handling through public write method
    @Test
    void testWriteWithUnsupportedFormat() {
        String[] formats = {"inv"};
        for (String format : formats) {
            ByteArrayOutputStream newStream = new ByteArrayOutputStream();
            OutputStreamImageSink newSink = new OutputStreamImageSink(newStream);
            newSink.setOutputFormatName(format);
            	
            Exception exception = assertThrows(IOException.class, () -> {newSink.write(testImage); });
            assertEquals("No suitable ImageWriter found for " + format + ".", exception.getMessage());
        }
    }
    
    @Test
    void isDefaultPngWriterTest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    	
		
			 Method isDefaultPngWriterMethod = OutputStreamImageSink.class.getDeclaredMethod("isDefaultPngWriter", ImageWriter.class);
			isDefaultPngWriterMethod.setAccessible(true);

//			 String[] formats = {"jpg", "png", "bmp"};
		      
//		     sink.setOutputFormatName(format);
			Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
			ImageWriter writer = writers.next();
			
			
	         // Test different cases
	         assertTrue((Boolean) isDefaultPngWriterMethod.invoke(sink, writer));
	         
	         
		
    }
    
    
}