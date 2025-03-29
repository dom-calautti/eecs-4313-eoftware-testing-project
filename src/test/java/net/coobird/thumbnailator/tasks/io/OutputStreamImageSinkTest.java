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

    // 1. Constructor Tests (Boundary Testing)
    @Test
    @DisplayName("Constructor should throw NullPointerException for null OutputStream")
    void testConstructorWithNull() {
        assertThrows(NullPointerException.class,
                () -> new OutputStreamImageSink(null),
                "OutputStream cannot be null"
        );
    }

    @Test
    @DisplayName("Constructor should accept valid OutputStream")
    void testConstructorWithValidStream() {
        assertDoesNotThrow(() -> new OutputStreamImageSink(new ByteArrayOutputStream()));
    }

    // 2. Format Detection Tests (Using Reflection)
    @Test
    @DisplayName("Test JPEG/BMP format detection")
    void testJpegBmpFormatDetection() throws Exception {
        Method isJpegOrBmpMethod = OutputStreamImageSink.class.getDeclaredMethod("isJpegOrBmp", String.class);
        isJpegOrBmpMethod.setAccessible(true);

        // Test different cases
        assertTrue((Boolean) isJpegOrBmpMethod.invoke(sink, "jpg"));
        assertTrue((Boolean) isJpegOrBmpMethod.invoke(sink, "jpeg"));
        assertTrue((Boolean) isJpegOrBmpMethod.invoke(sink, "bmp"));
        assertTrue((Boolean) isJpegOrBmpMethod.invoke(sink, "JPG"));
        assertTrue((Boolean) isJpegOrBmpMethod.invoke(sink, "JPEG"));
        assertTrue((Boolean) isJpegOrBmpMethod.invoke(sink, "BMP"));
        assertFalse((Boolean) isJpegOrBmpMethod.invoke(sink, "png"));
        assertFalse((Boolean) isJpegOrBmpMethod.invoke(sink, "gif"));
    }

    @Test
    @DisplayName("Test PNG format detection")
    void testPngFormatDetection() throws Exception {
        Method isPngMethod = OutputStreamImageSink.class.getDeclaredMethod("isPng", String.class);
        isPngMethod.setAccessible(true);

        assertTrue((Boolean) isPngMethod.invoke(sink, "png"));
        assertTrue((Boolean) isPngMethod.invoke(sink, "PNG"));
        assertFalse((Boolean) isPngMethod.invoke(sink, "jpg"));
        assertFalse((Boolean) isPngMethod.invoke(sink, "jpeg"));
    }

    // 3. Java Version Detection Test (Using Reflection)
    @Test
    @DisplayName("Test Java version detection")
    void testJavaVersionDetection() throws Exception {
        Method isJava9OrNewerMethod = OutputStreamImageSink.class.getDeclaredMethod("isJava9OrNewer");
        isJava9OrNewerMethod.setAccessible(true);

        boolean isJava9OrNewer = (Boolean) isJava9OrNewerMethod.invoke(sink);
        String version = System.getProperty("java.specification.version");

        if (version.contains(".")) {
            assertFalse(isJava9OrNewer, "Should be false for Java 8 or older");
        } 
    }

    // 4. Output Stream Access Test
    @Test
    @DisplayName("getSink should return the original OutputStream")
    void testGetSink() {
        assertEquals(outputStream, sink.getSink());
    }

    // 5. Write Method Tests
    @Test
    @DisplayName("Write should throw NullPointerException for null image")
    void testWriteWithNullImage() {
        assertThrows(NullPointerException.class,
                () -> sink.write(null),
                "Cannot write a null image"
        );
    }

    // Testing format handling through public write method
    @Test
    @DisplayName("Write should handle different formats")
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