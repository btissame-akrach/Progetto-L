package gui.ticketmachine;

import controller.TicketMachineSession;
import gui.BridgeSceneGrid;
import gui.WhiteBigButton;
import gui.WhiteSmallButton;
import items.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ticketmachine.TicketMachine;


public class BuySingleTicketScene extends BridgeSceneGrid{
    private TicketMachineSession controller;
    private Text text;
    private Button back;
    
    public BuySingleTicketScene(TicketMachine tMachine, TicketMachineSession controller){
        this.controller = controller;
        
        text = new Text("Choose a ticket");
        text.setFont(Font.font("Tahoma", FontWeight.SEMI_BOLD, 40));
        
        Separator hSeparator = new Separator();
        hSeparator.setOrientation(Orientation.HORIZONTAL);
        
        back = new WhiteSmallButton("Back");
        back.setOnAction(e->{
            controller.back();
        });
        
        istantiateGrid();
        add(text, 0, 0, 3, 1);
        add(hSeparator, 1, 0, 3, 1);
        addAllButtons(tMachine);
        
    }
    
    private void addAllButtons(TicketMachine tMachine) {
        List<Product> simpleTickets = getAllSimpleTickets(tMachine);
        int row = 0;
        int column = 0;
        
        for(Product product : simpleTickets) {
            Button button = new WhiteBigButton(product.getDescription() + "\n-\n" + product.getDuration() + " minutes");
            button.setOnAction(e -> {
                controller.startSale(product.getType());
            });
            add(button, row%3 + 2, column%3);
            column++;
            if(column%3 == 0)
                row++;
        }
        
        this.add(back, row%3 + 2, 3);
    }
    
    private List<Product> getAllSimpleTickets(TicketMachine tMachine) {
        List<Product> simpleTickets = new ArrayList();
        
        Map<String, Product> products = tMachine.getAvailableProducts();
        for(Map.Entry<String, Product> product : products.entrySet()) {
            if(isSimpleType(product.getValue()))
                simpleTickets.add(product.getValue());
        }
        
        return simpleTickets;
    }
    
    private boolean isSimpleType(Product p) {
        String type = p.getType();
        return type.charAt(0) == 'T';
    }
}
