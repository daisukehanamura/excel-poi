package com.example.excelpoi.Controller;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
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
		Sheet sheet = excelFileReader.readExcel(multipartFile,"SheetName");

		// 1行目取得
		Row row = sheet.getRow(0);

		// データがある最後のセル番号取得
		final int cellNum = row.getLastCellNum();
		// セル分ループ
		for (int cellIndex = 0; cellIndex < cellNum; ++cellIndex) {
			// セル取得
			Cell cell = row.getCell(cellIndex);

			// セルの値取得
			switch (cell.getCellType()) {
				case STRING:
					String stringVal = cell.getStringCellValue();
					// 文字列データの処理
					break;
				case NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						Date dateVal = cell.getDateCellValue();
						// 日付データの処理
					} else {
						double numVal = cell.getNumericCellValue();
						// 数値データの処理
					}
					break;
				default:
					// その他のデータタイプに対する処理
					break;
			}

			// 必要に応じた処理
		}

		response.message = "正常に完了しました";
		return response;
	}
}
