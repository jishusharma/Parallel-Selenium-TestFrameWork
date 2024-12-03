package interfaces;

import java.util.Set;

public interface WindowTabActions {
    void switchToWindow(String windowHandle);
    void switchToWindowByTitle(String title);
    void switchToWindowByUrl(String url);
    void switchToNewWindow();
    void closeCurrentWindowOrTab();
    void switchToParentWindow();
    void switchToFrame(int index);
    void switchToFrame(String nameOrId);
    void switchToDefaultContent();
    Set<String> getWindowHandles();
    String getCurrentWindowHandle();
    void createNewTab();
    void switchToTab(int index);
    int getNumberOfWindows();
    void maximizeWindow();
    void minimizeWindow();
    void setWindowSize(int width, int height);
}
