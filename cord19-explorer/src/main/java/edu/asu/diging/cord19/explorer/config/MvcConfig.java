package edu.asu.diging.cord19.explorer.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import edu.asu.diging.cord19.explorer.core.service.impl.ArxivAtomHttpConverter;

@Configuration
@ComponentScan("edu.asu.diging.simpleusers.web")
@EnableSpringDataWebSupport
public class MvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private ArxivAtomHttpConverter arxivConverter;

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        WebMvcConfigurer.super.extendMessageConverters(converters);
        HttpMessageConverter<?> toBeRemoved = null;
        for (HttpMessageConverter<?> conv : converters) {
            if(conv.getClass().equals(AtomFeedHttpMessageConverter.class)) {
                toBeRemoved = conv;
            }
        }
        if (toBeRemoved != null) {
            converters.remove(toBeRemoved);
        }
        converters.add(arxivConverter);
    }

}