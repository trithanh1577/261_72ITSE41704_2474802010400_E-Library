package com.elibrary.web;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentPathAdvice {

    @ModelAttribute("languageUrlEn")
    public String englishUrl(HttpServletRequest request) {
        return languageUrl(request, "en");
    }

    @ModelAttribute("languageUrlVi")
    public String vietnameseUrl(HttpServletRequest request) {
        return languageUrl(request, "vi");
    }

    private String languageUrl(HttpServletRequest request, String language) {
        String query = request.getQueryString();
        List<String> retainedParameters = new ArrayList<>();
        if (query != null && !query.isBlank()) {
            for (String parameter : query.split("&")) {
                String key = parameter.split("=", 2)[0];
                if (!"lang".equals(URLDecoder.decode(key, StandardCharsets.UTF_8))) {
                    retainedParameters.add(parameter);
                }
            }
        }
        retainedParameters.add("lang=" + language);
        return request.getRequestURI() + "?" + String.join("&", retainedParameters);
    }
}
