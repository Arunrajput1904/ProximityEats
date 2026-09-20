package com.arun.Restaurantbackend.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {


    private final Cloudinary cloudinary;

    @Async
    public String addImage(MultipartFile multipart,String name) throws IOException {


        Map upload = cloudinary.uploader().upload(multipart.getBytes(),
                ObjectUtils.asMap(
                        "folder", name
                        , "resource_type", "auto"
                ));
        return upload.get("secure_url").toString();
    }






}
