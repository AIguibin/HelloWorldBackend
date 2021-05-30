package com.aiguibin.springbootnative.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传实例
 *
 * @author AIguibin
 * Date time 2019年04月06日 21:08:43
 */
@Controller
@RequestMapping(value = "/file")
public class FileUploadController {
    //日志声明
    private static final Log logger = LogFactory.getLog(FileUploadController.class);


    @ResponseBody
    @RequestMapping(value = "/upload")
    public String upload(@RequestParam("upload")MultipartFile file){
        logger.debug(file.getOriginalFilename()+"已经上传成功");
        return "success";
    }
}
