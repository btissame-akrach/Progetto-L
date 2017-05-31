package gui;

import centralsystem.CSystem;
import centralsystem.Message;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Manuele
 */
public class MachineStatusPanel extends JPanel implements Observer{
    private Map<Integer, MachineLeafPanel> contents;
    private Box box;
    private JLabel activeLabel, idLabel, ipLabel, inkLvlLabel, paperLvlLabel;
    private JPanel labelPanel;
    
    public MachineStatusPanel(CSystem cSystem) {
        contents = new HashMap();
        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        box = Box.createVerticalBox();
        
        setupBox();
        
        this.add(box);
        cSystem.addObserver(this);
    }
    
    private void setupBox() {
        activeLabel = new JLabel("Active");
        idLabel = new JLabel("Id");
        ipLabel = new JLabel("Ip");
        inkLvlLabel = new JLabel("Ink Level");
        paperLvlLabel = new JLabel("Paper Level");
        
        labelPanel = new JPanel(new GridLayout(1,4));
        labelPanel.add(activeLabel);
        labelPanel.add(idLabel);
        labelPanel.add(ipLabel);
        labelPanel.add(inkLvlLabel);
        labelPanel.add(paperLvlLabel);
        
        box.add(labelPanel);
    }
    
    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof Message) {
            decodeRead(((Message)arg).getMessage());
        }
    }
    
    private boolean alredyHas(int id) {
        return contents.containsKey(id);
    }
    
    private void decodeRead(String inputData) {
        JSONObject obj;
        
        try {
            JSONParser parser = new JSONParser();
            obj = (JSONObject) parser.parse(inputData);
            String method = null;
            if(obj.containsKey("method")) 
                method = ((String) obj.get("method")).trim().toUpperCase();

            if(method != null && method.equals("UPDATEMACHINESTATUS")) {
                JSONObject data = (JSONObject) obj.get("data");
                
                double id = (double)data.get("machineCode");
                double inkLvl = (double)data.get("inkLevel");
                double paperLvl = (double)data.get("paperLevel");
                boolean active = (boolean)data.get("active");
                String ip = (String)data.get("ipAddress");
                
                if(alredyHas((int)id)) {
                    contents.get((int)Math.round(id)).updateInkLevel((int)Math.round(inkLvl));
                    contents.get((int)Math.round(id)).updatePaperLevel((int)Math.round(paperLvl));
                }
                else {
                    MachineLeafPanel newPanel = new MachineLeafPanel(id + "", inkLvl, paperLvl, ip);
                    box.add(newPanel);
                    contents.put((int)id, newPanel);
                }
                if(!active) contents.get((int)Math.round(id)).colorImage(Color.RED);
                this.revalidate();
                this.repaint();
            }
        } 
        
        catch (ParseException ex) {
            System.err.println("Error: packet parsing error " + inputData);
        }
    }
}