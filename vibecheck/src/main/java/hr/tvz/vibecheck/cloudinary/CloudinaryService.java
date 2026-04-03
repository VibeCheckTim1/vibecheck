package hr.tvz.vibecheck.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import hr.tvz.vibecheck.dto.response.ImageUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public ImageUploadResponse uploadUserAvatar(MultipartFile file, Long userId) throws IOException {
        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "VibeCheck/avatars",
                        "public_id", "user_" + userId + "_avatar",
                        "overwrite", true,
                        "resource_type", "image"
                )
        );

        String imageUrl = result.get("secure_url").toString();
        String publicId = result.get("public_id").toString();

        return new ImageUploadResponse(imageUrl, publicId);

    }

    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }


}
