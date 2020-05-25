package sci.changecostcenter.Control;

import Entity.Executavel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import sci.changecostcenter.Model.CostCenterModel;
import sci.changecostcenter.Model.Entity.ContabilityEntry;
import sql.Database;

public class Controller {

    private static String reference = "202001";
    private static List<ContabilityEntry> contabilityEntries = new ArrayList<>();

    public class defineDatabase extends Executavel {

        public defineDatabase() {
            nome = "Definindo banco de dados...";
        }

        @Override
        public void run() {
            Database.setStaticObject(new Database(new File("sci.cfg")));
        }
    }

    public class getContabilityEntries extends Executavel {

        public getContabilityEntries() {
            nome = "Buscando lançamentos contábeis sem centro de custo";
        }

        @Override
        public void run() {
            contabilityEntries = CostCenterModel.getContabilityEntriesWithoutCostCenter(reference);
        }
    }
}
