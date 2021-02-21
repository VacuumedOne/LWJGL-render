package oz.common.io;

import java.nio.file.Paths;
import java.io.File;

public class DefaultFolderLocationProvider {
    public static String getProjectRoot() {
        String current = Paths.get("").toAbsolutePath().toString(); // current directory
        while (true) { // search .root file path
            File file = new File(Paths.get(current, ".root").toString());
            if (file.exists()) {
                return current;
            }
            if (Paths.get(current).getNameCount() == 0) {
                System.err.println("Unable to determine project root. Please confirm .root file is exist in project root folder");
                System.exit(1);
            } else {
                current = new File(current).getParent();
            }
        }
    }
}
