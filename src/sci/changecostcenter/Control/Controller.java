package sci.changecostcenter.Control;

import Entity.Executavel;
import java.io.File;
import sql.Database;

public class Controller {
    public class defineDatabase extends Executavel{

        public defineDatabase() {
            nome = "Definindo banco de dados...";
        }

        @Override
        public void run() {
            Database.setStaticObject(new Database(new File("sci.cfg")));
        }        
    }
}
