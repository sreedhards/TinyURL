package tinyurl.app.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class URLRepository {

    private final String idKey;
    private final String urlKey;
    private final String urlStatKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(URLRepository.class);


    @Autowired
    private RedisTemplate redisTemplate;

    public URLRepository() {
        this.idKey = "id";
        this.urlKey = "url:";
        this.urlStatKey = "urlstat:";
    }

    public URLRepository(String idKey, String urlKey, String urlStatKey) {
        this.idKey = idKey;
        this.urlKey = urlKey;
        this.urlStatKey = urlStatKey;
    }

    public Long nextID() {
        HashOperations hashOperations = redisTemplate.opsForHash();
        Long id = hashOperations.increment(idKey, 1, 1);

        LOGGER.info("Incrementing ID: {}", id - 1);
        return id - 1;
    }

    public void saveUrl(String key, String longUrl) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        LOGGER.info("Saving: {} at {}", longUrl, key);
        hashOperations.put(urlKey, key, longUrl);
        hashOperations.put(urlStatKey, key, 0);

    }

    public String getUrl(Long id) throws Exception {
        HashOperations hashOperations = redisTemplate.opsForHash();
        LOGGER.info("Retrieving at {}", id);

        String url = (String) hashOperations.get(urlKey, "url:" + id);
        LOGGER.info("Retrieved {} at {}", url, id);
        if (url == null) {
            throw new Exception("URL at key" + id + " does not exist");
        } else {
            hashOperations.put(urlStatKey, "url:" + id, (Integer) hashOperations.get(urlStatKey, "url:" + id) + 1);
        }
        return url;
    }

    public String getUrlStats(Long id) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        LOGGER.info("Retrieving usage at {}", id);
        String count = String.valueOf(hashOperations.get(urlStatKey, "url:" + id));
        LOGGER.info("Retrieved count for {} is {}", id, count);

        return count;
    }
    public String getCountOfUrls() {
        HashOperations hashOperations = redisTemplate.opsForHash();
        List<Integer> total = null;
        total = hashOperations.values(urlKey);
        LOGGER.info("Retrieved url count is {}", total.size());

        return String.valueOf(total.size());
    }


}
