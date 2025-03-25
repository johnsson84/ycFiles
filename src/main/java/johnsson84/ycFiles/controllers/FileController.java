package johnsson84.ycFiles.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FileController {

    // Upload a file
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/upload/{user}/{folder}")
    public String uploadFile(@PathVariable("user") String user, @PathVariable("folder") String folder, @RequestParam("file") MultipartFile file) throws IOException {

        // Save path
        String userMail = !user.isEmpty() ? user : "noname";
        String folderName = !folder.isEmpty() ? folder : "Upload";
        String folderPath = String.format("%s/files/%s/%s/", System.getProperty("user.dir"), userMail, folderName);
        File dir = new File(folderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileUploadStatus = "";

        try {
            // Create object
            FileOutputStream fileOut = new FileOutputStream(folderPath + file.getOriginalFilename());
            fileOut.write(file.getBytes());
            // Close connection
            fileOut.close();
            fileUploadStatus = "File uploaded successfully";
        } catch (Exception e) {
            e.printStackTrace();
            fileUploadStatus = "Error in uploading file: " + e;
        }
        return fileUploadStatus;
    }

    // Get list of files
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/getFiles/{user}/{folder}")
    public String[] getFiles(@PathVariable("user") String user, @PathVariable("folder") String folder) {

        String folderPath = String.format("%s/files/%s/%s/", System.getProperty("user.dir"), user, folder);

        File directory = new File(folderPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String[] files = directory.list();
        return files;
    }
}
