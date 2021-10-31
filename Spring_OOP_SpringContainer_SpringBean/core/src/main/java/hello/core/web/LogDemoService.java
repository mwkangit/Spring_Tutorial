package hello.core.web;

import hello.core.common.MyLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogDemoService {

    // ObjectProvider로 의존관계 주입 지연
//    private final ObjectProvider<MyLogger> myLoggerProvider;

    // Proxy로 바로 의존관계 성립
    private final MyLogger myLogger;

    public void logic(String id){
//        MyLogger myLogger = myLoggerProvider.getObject();
        myLogger.log("service id = " + id);
    }
}
