package com.example.excelpoi.ExcelUtil;

import org.springframework.web.multipart.MultipartFile;

import com.example.excelpoi.Controller.FileUploadController.UploadResponse;

public class ExcelDataValidator {
    
    /*
     * Excelファイルの存在チェック
     */
    public UploadResponse checkNotNullExcelFile(MultipartFile multipartFile,UploadResponse response){

        // ファイルが空の場合は異常終了
		if(multipartFile.isEmpty()){
			// 異常終了時の処理
            response.status = 0;
			response.message = "アップロードされたエクセルファイルが存在しなかった";
		}

        return response;
    }

    
}
