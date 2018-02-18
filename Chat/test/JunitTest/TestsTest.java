/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JunitTest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author refael92
 */
public class TestsTest {

    public TestsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
       serverchat.ServerGui server = new serverchat.ServerGui();
       server.setVisible(true);
       server.StartBtn.doClick();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void runClient() {
        String name = "refael";
        chatproject.ClientGui client = new chatproject.ClientGui();
        client.setVisible(true);
        client.NameField.setText(name);
        client.ConnectBtn.doClick();
        while (client.isConnected) {
            if (!client.chatWin.getText().equals("")) {
                assertEquals(name + " is connected", client.chatWin.getText().trim());
            break;
            }
        }
    }
}
