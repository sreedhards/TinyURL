package tinyurl.app.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import tinyurl.app.service.URLUtil;
import tinyurl.app.service.URLMappingService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;


@RestController
public class URLController {
    private static final Logger LOGGER = LoggerFactory.getLogger(URLController.class);
    private final URLMappingService urlMappingService;

    public URLController(URLMappingService urlMappingService) {
        this.urlMappingService = urlMappingService;
    }

    @RequestMapping(value = "/tiny", method = RequestMethod.POST, consumes = {"application/json"})
    public String createTinyUrl(@RequestBody @Valid final LongURL longURL, HttpServletRequest request) throws Exception {
        LOGGER.info("Received url to create tiny: " + longURL.getUrl());
        String longUrl = longURL.getUrl();
        if (URLUtil.INSTANCE.validateURL(longUrl)) {
            String localURL = request.getRequestURL().toString();
            String tinyUrl = urlMappingService.getTinyURL(localURL, longURL.getUrl());
            LOGGER.info("Tiny url is: " + tinyUrl);
            return tinyUrl;
        }
        throw new Exception("Please enter a valid URL");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public RedirectView getOriginalUrl(@PathVariable String id) throws IOException, URISyntaxException, Exception {
        LOGGER.info("Received tiny url to redirect: " + id);
        String redirectUrlString = urlMappingService.getLongURLFromID(id);
        LOGGER.info("Original URL: " + redirectUrlString);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("http://" + redirectUrlString);
        return redirectView;
    }

    @RequestMapping(value = "/stats/", method = RequestMethod.GET)
    public String totalTinyUrl() throws IOException, URISyntaxException, Exception {
        LOGGER.info("Received req to count total urls");
        String countInfo = "Count of tiny urls is : " + urlMappingService.getCountOfURL();
        LOGGER.info(countInfo);

        return countInfo;
    }
    @RequestMapping(value = "/stats/{id}", method = RequestMethod.GET)
    public String countReqForTinyUrl(@PathVariable String id) throws IOException, URISyntaxException, Exception {
        LOGGER.info("Received stat req: " + id);
        String countInfo = "Count of requests for this URL is : " + urlMappingService.getCountFromID(id);
        LOGGER.info(countInfo);

        return countInfo;
    }


}

class LongURL {
    private String url;

    @JsonCreator
    public LongURL() {

    }

    @JsonCreator
    public LongURL(@JsonProperty("url") String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}


