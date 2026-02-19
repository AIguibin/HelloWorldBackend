package com.aiguibin.platform.arch.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

class ResultVOTest {

    @Test
    void testSuccessWithoutData() {
        ResultVO<Void> result = ResultVO.success();
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
        assertTrue(result.isSuccess());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testSuccessWithData() {
        String testData = "测试数据";
        ResultVO<String> result = ResultVO.success(testData);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(testData, result.getData());
        assertTrue(result.isSuccess());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testSuccessWithMessageAndData() {
        String message = "操作成功";
        String testData = "测试数据";
        ResultVO<String> result = ResultVO.success(message, testData);
        assertEquals(200, result.getCode());
        assertEquals(message, result.getMessage());
        assertEquals(testData, result.getData());
        assertTrue(result.isSuccess());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testErrorWithCodeAndMessage() {
        Integer errorCode = 400;
        String errorMessage = "请求参数错误";
        ResultVO<String> result = ResultVO.error(errorCode, errorMessage);
        assertEquals(errorCode, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        assertFalse(result.isSuccess());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testErrorWithMessage() {
        String errorMessage = "服务器内部错误";
        ResultVO<String> result = ResultVO.error(errorMessage);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        assertFalse(result.isSuccess());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testErrorWithCodeMessageAndData() {
        Integer errorCode = 404;
        String errorMessage = "资源不存在";
        String errorData = "错误详情";
        ResultVO<String> result = ResultVO.error(errorCode, errorMessage, errorData);
        assertEquals(errorCode, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertEquals(errorData, result.getData());
        assertFalse(result.isSuccess());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testIsSuccessWithCode200() {
        ResultVO<String> result = ResultVO.success("测试数据");
        assertTrue(result.isSuccess());
    }

    @Test
    void testIsSuccessWithCodeNot200() {
        ResultVO<String> result = ResultVO.error(400, "错误");
        assertFalse(result.isSuccess());
    }

    @Test
    void testConstructorWithCodeAndMessage() {
        Integer code = 200;
        String message = "成功";
        ResultVO<String> result = new ResultVO<>(code, message);
        assertEquals(code, result.getCode());
        assertEquals(message, result.getMessage());
        assertNull(result.getData());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testConstructorWithCodeMessageAndData() {
        Integer code = 200;
        String message = "成功";
        String data = "测试数据";
        ResultVO<String> result = new ResultVO<>(code, message, data);
        assertEquals(code, result.getCode());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testDefaultConstructor() {
        ResultVO<String> result = new ResultVO<>();
        assertNull(result.getCode());
        assertNull(result.getMessage());
        assertNull(result.getData());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testSuccessWithComplexObject() {
        List<String> testData = Arrays.asList("item1", "item2", "item3");
        ResultVO<List<String>> result = ResultVO.success(testData);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(testData, result.getData());
        assertTrue(result.isSuccess());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testSuccessWithNullData() {
        ResultVO<String> result = ResultVO.success(null);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
        assertTrue(result.isSuccess());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testErrorWithNullMessage() {
        ResultVO<String> result = ResultVO.error((String) null);
        assertEquals(500, result.getCode());
        assertNull(result.getMessage());
        assertNull(result.getData());
        assertFalse(result.isSuccess());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testSettersAndGetters() {
        ResultVO<String> result = new ResultVO<>();
        result.setCode(200);
        result.setMessage("测试消息");
        result.setData("测试数据");
        result.setTraceId("trace-123");
        
        assertEquals(200, result.getCode());
        assertEquals("测试消息", result.getMessage());
        assertEquals("测试数据", result.getData());
        assertEquals("trace-123", result.getTraceId());
    }
}