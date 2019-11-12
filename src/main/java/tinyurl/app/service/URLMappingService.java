package tinyurl.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tinyurl.app.repository.URLRepository;


@Service
public class URLMappingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(URLMappingService.class);
    private final URLRepository urlRepository;

    @Autowired
    public URLMappingService(URLRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String getTinyURL(String localURL, String longUrl) {
        LOGGER.info("Shortening {}", longUrl);
        Long id = urlRepository.nextID();
        String uniqueID = URLUtil.INSTANCE.createUrlID(id);
        urlRepository.saveUrl("url:" + id, longUrl);
        String baseString = getBaseURL(localURL);
        String tinyURL = baseString + uniqueID;
        return tinyURL;
    }

    public String getLongURLFromID(String uniqueID) throws Exception {
        Long dictionaryKey = URLUtil.INSTANCE.getDictionaryKeyFromUniqueID(uniqueID);
        String longUrl = urlRepository.getUrl(dictionaryKey);
        LOGGER.info("Converting tiny URL back to {}", longUrl);
        return longUrl;
    }

    public String getCountFromID(String uniqueID) {
        Long dictionaryKey = URLUtil.INSTANCE.getDictionaryKeyFromUniqueID(uniqueID);
        String numberOfReq = urlRepository.getUrlStats(dictionaryKey);
        LOGGER.info("Count of requests for id {} is {}", uniqueID, numberOfReq);
        return numberOfReq;
    }

    public String getCountOfURL() {
        return urlRepository.getCountOfUrls();
    }

    private String getBaseURL(String localURL) {
        String[] urlComponents = localURL.split("/");
        // remove the endpoint (last index), .i.e remove "tiny" from  http://localhost:8080/tiny
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < urlComponents.length - 1; ++i) {
            sb.append(urlComponents[i]);
        }
        sb.append('/');
        return sb.toString();
    }


}
