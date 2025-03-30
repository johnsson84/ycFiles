package johnsson84.ycFiles.controllers;

import johnsson84.ycFiles.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    final private FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // Upload a file
    @PostMapping("/upload/{user}/{folder}")
    public ResponseEntity<?> uploadFile(@PathVariable("user") String user, @PathVariable("folder") String folder, @RequestParam("file") MultipartFile file) throws IOException {
        String uploaded = fileService.uploadFile(user, folder, file);
        return ResponseEntity.ok().body(uploaded);
    }

    // Get list of files
    @GetMapping("/getFiles/{user}/{folder}")
    public String[] getFiles(@PathVariable("user") String user, @PathVariable("folder") String folder) {
        return fileService.getFiles(user, folder);
    }

    // Add a folder
    @PostMapping("/addFolder/{user}/{folderName}")
    public ResponseEntity<?> addFolder(@PathVariable("user") String user, @PathVariable("folderName") String folderName) {
        return ResponseEntity.ok(fileService.addFolder(user, folderName));
    }

    // Get list of folders
    @GetMapping("/getFolders/{user}")
    public List<String> getFolders(@PathVariable("user") String user) {
        return fileService.getFolders(user);
    }
}
