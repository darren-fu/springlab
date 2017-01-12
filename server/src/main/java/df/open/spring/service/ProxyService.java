package df.open.spring.service;

/**
 * 说明:
 * <p/>
 * Copyright: Copyright (c)
 * <p/>
 * Company:
 * <p/>
 *
 * @author darren-fu
 * @version 1.0.0
 * @contact 13914793391
 * @date 2016/11/22
 */
public interface ProxyService {
    String getStatus();

    Integer getAge(Long id, String name);
    int getHeight(Long id);
}
