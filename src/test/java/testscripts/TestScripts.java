package testscripts;
import java.io.IOException;

import org.testng.annotations.Test;

import com.app.keywords.*;
import com.web.base.DriverScript;


public class TestScripts extends DriverScript {
    @Test
    public void flow_AFB_Create_User_FS_AT043() throws InterruptedException, IOException {
        String testCaseID = "TC001";
        if (isTestCaseRunnable(testCaseID)) {
            Keywords.login();
        }

    }
}