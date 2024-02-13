package com.example.excelpoi.Controller;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.excelpoi.ExcelUtil.ExcelDataValidator;
import com.example.excelpoi.ExcelUtil.ExcelFileReader;

@Controller
@RequestMapping("/file")
public class FileUploadController {

	private final ExcelDataValidator excelDataValidator;
	private final ExcelFileReader excelFileReader;

	public FileUploadController(ExcelDataValidator excelDataValidator,ExcelFileReader excelFileReader){
    this.excelDataValidator = excelDataValidator;
    this.excelFileReader = excelFileReader;
	}

	public class UploadResponse {
		public int status = 1;
		public String message = "success";
	}

	@RequestMapping("/upload")
	public String upload() {
		return "upload";
	}

	@PostMapping(value = "/upload")
	@ResponseBody
	public UploadResponse uploadFile(@RequestParam("upload_file") MultipartFile multipartFile) throws Exception {

		// アップロード状態STS
		UploadResponse response = new UploadResponse();

		// ExcelファイルNULLチェック
		response = excelDataValidator.checkNotNullExcelFile(multipartFile, response);
		if(response.status != 1){
			return response;
		}

		// Excelシート読み込み
		Sheet sheet = excelFileReader.readExcel(multipartFile,"Sheet1");
		Class<?> clazz = Class.forName("com.example.excelpoi.Entity.Sample");
		List<Object> object = excelFileReader.readFromXml(
			sheet, "C:\\Users\\PC\\git\\excel-poi\\excel-poi\\XML\\sample.xml", clazz);
		System.out.println(object);

		response.message = "正常に完了しました";
		return response;
	}
}
