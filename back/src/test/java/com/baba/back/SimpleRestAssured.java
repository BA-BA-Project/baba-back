package com.baba.back;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import org.springframework.http.MediaType;

public class SimpleRestAssured {

    public static ExtractableResponse<Response> get(String path) {
        return get(path, null);
    }

    public static ExtractableResponse<Response> get(String path, Map<String, String> headers) {
        return thenExtract(givenWithHeaders(headers).when().get(path));
    }

    public static ExtractableResponse<Response> post(String path, Object request) {
        return post(path, null, request);
    }

    public static ExtractableResponse<Response> post(String path, Map<String, String> headers) {
        return post(path, headers, null);
    }

    public static ExtractableResponse<Response> post(String path, Map<String, String> headers, Object request) {
        final RequestSpecification given = givenWithHeaders(headers);
        if (request != null) {
            given.body(request);
        }

        return thenExtract(given.contentType(MediaType.APPLICATION_JSON_VALUE).when().post(path));
    }

    public static ExtractableResponse<Response> put(String path, Map<String, String> headers, Object request) {
        final RequestSpecification given = givenWithHeaders(headers);
        if (request != null) {
            given.body(request);
        }

        return thenExtract(given.contentType(MediaType.APPLICATION_JSON_VALUE).when().put(path));
    }

    public static ExtractableResponse<Response> patch(String path, Map<String, String> headers, Object request) {
        final RequestSpecification given = givenWithHeaders(headers);
        if (request != null) {
            given.body(request);
        }

        return thenExtract(given.contentType(MediaType.APPLICATION_JSON_VALUE).when().patch(path));
    }

    public static ExtractableResponse<Response> delete(String path, Map<String, String> headers) {
        final RequestSpecification given = givenWithHeaders(headers);

        return thenExtract(given.contentType(MediaType.APPLICATION_JSON_VALUE).when().delete(path));
    }

    private static RequestSpecification givenWithHeaders(Map<String, String> headers) {
        final RequestSpecification given = given();
        if (headers != null) {
            given.headers(headers);
        }
        return given;
    }

    private static RequestSpecification given() {
        return RestAssured.given().log().all();
    }

    public static ExtractableResponse<Response> thenExtract(Response response) {
        return response.then().log().all().extract();
    }

    public static <T> T toObject(ExtractableResponse<Response> response, Class<T> clazz) {
        return response.as(clazz);
    }
}
