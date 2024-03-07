import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ImageServiceTest {

    @Test
    void testUploadAndRemoveProfilePicture() {
        ImageService imageService = new ImageService();
        String userId = "123";
        String imagePath = "path/to/image.jpg";
        
        boolean uploadResult = imageService.uploadProfilePicture(userId, imagePath);
        assertTrue(uploadResult);
        
        boolean removeResult = imageService.removeProfilePicture(userId);
        assertTrue(removeResult);
    }
}
