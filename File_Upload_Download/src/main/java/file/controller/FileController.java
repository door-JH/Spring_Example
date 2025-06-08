package file.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
public class FileController {
	
	@RequestMapping(value = "/file-test", method = RequestMethod.GET)
	public String home() {
		log.info("FileContoller : /file-test");
		return "file-test";
	}
	
	@PostMapping(value = "/file-upload")
	@ResponseBody
	public String uploadFile(@RequestPart("file") MultipartFile file) throws IOException {
		log.info("FileContoller : /file-upload");
		// 파일 정보
		System.out.println(file.getContentType());
		System.out.println(file.getName());
		System.out.println(file.getOriginalFilename());
		System.out.println(file.getSize());

		// 파일 저장 위치 설정 : savePath -
		String savePath = "D:\\downloads";
		
		// UUID + 기본 파일 이름
		String uuid = UUID.randomUUID().toString();
		String fileName = uuid + "_" + file.getOriginalFilename();
		
		// 파일 실제 저장

		if(!new File(savePath).exists()) {
			new File(savePath).mkdirs();
		}

		file.transferTo(new File(savePath + "//" + fileName));
		
		return "success : file-upload";
	}
	
	@GetMapping("/file-download")
	public ResponseEntity<Resource> downloadFile(){
		log.info("FileContoller : /file-download");
		
		// 지정 경로의 특정 파일 선택 -> resource로 변경 -> HTTP Header 컨텐츠의 타입 지정 후 전달 
		Path path = Paths.get("D:\\downloads\\191d5b96-2f2c-4396-b8d3-f9042321a1ae_Test.txt");
		Resource resource = null;

		try {
			resource = new InputStreamResource(Files.newInputStream(path));
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		// MDN MIME TYPE
		// Content-Type : APPLICATION_OCTET_STREAM
		// ContentDisposition : 컨텐츠의 표시를 지정하는 속성 - 파일의 경우 파일이름(확장자포함)으로 지정 

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDisposition(ContentDisposition
											.builder("attachment")
											.filename("Test.txt")
											.build());


		return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
	}

	@GetMapping("/file-delete")
	@ResponseBody
	public String deleteFile(){

		//savePath에 존재하는 파일이 존재한다면 -> "file deleted"
		File targetfile = new File("D:\\downloads\\191d5b96-2f2c-4396-b8d3-f9042321a1ae_Test.txt");

		if(targetfile.exists()){
			targetfile.delete();
		}

		return "success : file-delete";
	}


}
