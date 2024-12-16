module com.pymechat {
    requires javafx.controls;
    requires javafx.fxml;
	requires com.google.gson;
    requires javafx.graphics;
    requires org.mongodb.driver.sync.client;

    opens com.pymechat.services to com.google.gson;
    opens com.pymechat.models to com.google.gson;
    opens com.pymechat to javafx.fxml;
    exports com.pymechat;
}
