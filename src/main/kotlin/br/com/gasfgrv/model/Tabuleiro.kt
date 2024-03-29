package br.com.gasfgrv.model

import java.util.*

class Tabuleiro(val qtdeLinhas: Int, val qtdeColunas: Int, val qtdeMinas: Int) {

    private val campos = ArrayList<ArrayList<Campo>>()
    private val callbacks = ArrayList<(TabuleiroEvento) -> Unit>()

    init {
        gerarCampos()
        associarVizinhos()
        sortearMinas()
    }

    private fun gerarCampos() {
        for (linha in 0 until qtdeLinhas) {
            campos.add(ArrayList())
            gerarCamposColunas(linha)
        }
    }

    private fun gerarCamposColunas(linha: Int) {
        for (coluna in 0 until qtdeColunas) {
            val novoCampo = Campo(linha, coluna)
            novoCampo.onEvento { _, evento -> this.verificarVitoriaDerrota(evento) }
            campos[linha].add(novoCampo)
        }
    }

    private fun associarVizinhos() {
        forEachCampo { associarVizinhos(it) }
    }

    private fun associarVizinhos(campo: Campo) {
        val (linha, coluna) = campo
        val linhas = arrayOf(linha - 1, linha, linha + 1)
        val colunas = arrayOf(coluna - 1, coluna, coluna + 1)

        linhas.forEach { l ->
            colunas.forEach { c ->
                val atual = campos.getOrNull(l)?.getOrNull(c)
                atual?.takeIf { campo != it }?.let { campo.addVizinho(it) }
            }
        }
    }

    private fun sortearMinas() {
        val gerador = Random()

        var linhaSorteada: Int
        var colunaSorteada: Int
        var quantidadeMinasAtual = 0

        while (quantidadeMinasAtual < this.qtdeMinas) {
            linhaSorteada = gerador.nextInt(qtdeLinhas)
            colunaSorteada = gerador.nextInt(qtdeColunas)

            val campoSorteado = campos[linhaSorteada][colunaSorteada]

            if (campoSorteado.seguro) {
                campoSorteado.minar()
                quantidadeMinasAtual++
            }
        }
    }

    private fun objetivoAlcancado(): Boolean {
        var jogadorGanhou = true
        forEachCampo { if (!it.objetivoAlcancado) jogadorGanhou = false }
        return jogadorGanhou
    }

    private fun verificarVitoriaDerrota(evento: CampoEvento) {
        if (evento == CampoEvento.EXPLOSAO) {
            callbacks.forEach { it(TabuleiroEvento.DERROTA) }
        } else if (objetivoAlcancado()) {
            callbacks.forEach { it(TabuleiroEvento.VITORIA) }
        }
    }

    fun forEachCampo(callback: (Campo) -> Unit) {
        campos.forEach { linha -> linha.forEach(callback) }
    }

    fun onEvento(callback: (TabuleiroEvento) -> Unit) {
        callbacks.add(callback)
    }

    fun reiniciar() {
        forEachCampo { it.reiniciar() }
        sortearMinas()
    }

}
