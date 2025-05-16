import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.CheckBox
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import javafx.scene.layout.HBox
import javafx.scene.web.WebView
import javafx.stage.Stage

class Main : Application() {
    private val webView = WebView()
    private val textArea = TextArea()
    private val checkBoxGroup : Group = Group()
    private val verticesCheckBoxes: MutableList<CheckBox> = mutableListOf()
    private val verticesDisabled: MutableList<String> = mutableListOf()
    private val verticesNames : MutableList<String> = mutableListOf()
    private val vbox2 : HBox = HBox()
    private val vbox : VBox = VBox()

    override fun start(primaryStage: Stage) {
        val w = 1200.0
        val h = 600.0
        addInputPanelElements(vbox)

        val scene = Scene(vbox, w, h)
        primaryStage.title = "Directed graphs diagram"
        primaryStage.scene = scene
        primaryStage.isResizable = true
        primaryStage.show()
    }

    private fun addVertextoList(vertex : String) {
        verticesNames.add(vertex)
        val c = CheckBox(vertex)
        c.isSelected = true
        c.style = "-fx-font-size: 16px;";
        c.setOnAction({ event ->
            if (c.isSelected()) {
                verticesDisabled.remove(c.text)
            } else {
                verticesDisabled.add(c.text)
            }
            textToGraph()
        })
        verticesCheckBoxes.add(c)
        vbox2.children.add(c)
        vbox.children[2] = vbox2
    }

    private fun textToGraph(){
        val edges = Regex("[A-Za-z0-9]+-->[A-Za-z0-9]+")
        val matches = edges.findAll(textArea.text)
        val edgesList = matches.map { it.value }.toList()

        // da li je neko izbacen iz textArea
        val setVertex : MutableSet<String> = mutableSetOf()
        edgesList.forEach {setVertex.addAll(it.split("-->"))}
        if(setVertex.size < verticesNames.size) {
            val diff = verticesNames.toSet() - setVertex
            for (d in diff) {
                val index = verticesNames.indexOf(d)
                vbox2.children.remove(verticesCheckBoxes.get(index))
                verticesNames.removeAt(index)
                verticesCheckBoxes.removeAt(index)
            }

        }
        //***************************

        var input = "graph LR;\n"
        for (e in edgesList) {
            val parts = e.split("-->")

            if(!verticesNames.contains(parts[0])) {
                addVertextoList(parts[0])
            }
            if(!verticesNames.contains(parts[1])) {
                addVertextoList(parts[1])
            }

            if(!verticesDisabled.contains(parts[0]) && !verticesDisabled.contains(parts[1])){
                input += e
                input += ";\n"
            }
        }
        updateDiagram(input)
    }

    private fun addInputPanelElements(vbox: VBox) {

        textArea.setText( "List the edges as such: A-->B")
        textArea.textProperty().addListener { observable, oldValue, newValue ->
            textToGraph()
        }
        vbox2.children.add(checkBoxGroup)
        vbox.children.addAll(textArea,webView,vbox2)
    }

    private fun updateDiagram(text: String){

        val graphHTML = "<!doctype html>\n" +
                "<html lang=\"en\">\n" +
                "  <body>\n" +
                "    <pre class=\"mermaid\">\n" +
                text +
                "    </pre>\n" +
                "    <script type=\"module\">\n" +
                "import mermaid from 'https://cdn.jsdelivr.net/npm/mermaid@11/dist/mermaid.esm.min.mjs';" +
                "mermaid.initialize({ startOnLoad: true });" +
                "    </script>\n" +
                "  </body>\n" +
                "</html>".trimIndent()
        webView.engine.loadContent(graphHTML)
    }
}

fun main() {
    Application.launch(Main::class.java)
}