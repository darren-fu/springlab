package df.open.spring.service;

import df.open.annotation.GenerateImpl;

/**
 * Created by darrenfu on 17-4-1.
 */
@GenerateImpl
public interface SimpleService {
    Response<Data<ProxyService>> doTest();
}
