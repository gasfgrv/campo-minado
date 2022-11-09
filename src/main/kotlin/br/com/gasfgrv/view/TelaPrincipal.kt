package br.com.gasfgrv.view

import br.com.gasfgrv.model.Tabuleiro
import br.com.gasfgrv.model.TabuleiroEvento
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

class TelaPrincipal : JFrame() {

    private val tabuleiro = Tabuleiro(qtdeLinhas = 16, qtdeColunas = 30, qtdeMinas = 89)
    private val painelTabuleiro = PainelTabuleiro(tabuleiro)

    init {
        tabuleiro.onEvento(this::mostrarResultado)
        add(painelTabuleiro)

        setSize(690, 438)
        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
        title = "Campo Minado"
        isVisible = true
    }

    private fun mostrarResultado(evento: TabuleiroEvento) {
        SwingUtilities.invokeLater {
            val mensagem = when (evento) {
                TabuleiroEvento.VITORIA -> "Você ganhou"
                TabuleiroEvento.DERROTA -> "Você perdeu"
            }

            JOptionPane.showMessageDialog(this, mensagem)
            tabuleiro.reiniciar()

            painelTabuleiro.repaint()
            painelTabuleiro.validate()
        }
    }

}