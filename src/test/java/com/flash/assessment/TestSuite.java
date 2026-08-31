package com.flash.assessment;

import com.flash.assessment.integration.AdminIntegrationTest;
import com.flash.assessment.integration.SanitizationIntegrationTest;
import com.flash.assessment.unit.AdminControllerTest;
import com.flash.assessment.unit.MessageControllerTest;
import com.flash.assessment.unit.SanitizerServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AdminControllerTest.class,
        MessageControllerTest.class,
        SanitizerServiceTest.class,
        AdminIntegrationTest.class,
        SanitizationIntegrationTest.class
})

public class TestSuite {
}
