package br.com.gasfgrv.model

data class Campo(val linha: Int, val coluna: Int) {

    private val vizinhos = ArrayList<Campo>()
    private val callbacks = ArrayList<(Campo, CampoEvento) -> Unit>()

    var marcado = false
    var aberto = false
    var minado = false

    val desmarcado get() = !marcado
    val fechado get() = !aberto
    val seguro get() = !minado
    val objetivoAlcancado get() = seguro && aberto || minado && marcado
    val quantidadeDeVizinhosMarcado get() = vizinhos.filter { it.minado }.size
    val vizinhancaSegura get() = vizinhos.map { it.seguro }.reduce { resultado, seguro -> resultado && seguro }

    fun addVizinho(vizinho: Campo) {
        vizinhos.add(vizinho)
    }

    fun onEvento(callback: (Campo, CampoEvento) -> Unit) {
        callbacks.add(callback)
    }

    fun abrir() {
        if (fechado) {
            aberto = true
        }

        if (minado) {
            callbacks.forEach { it(this, CampoEvento.EXPLOSAO) }
        } else {
            callbacks.forEach { it(this, CampoEvento.ABERTURA) }
            vizinhos.filter { it.fechado && it.seguro && vizinhancaSegura }.forEach { it.abrir() }
        }
    }

    fun alteracaoMarcacao() {
        if (fechado) {
            marcado = !marcado
            val evento = if (marcado) CampoEvento.MARCACAO else CampoEvento.DESMARCACAO
            callbacks.forEach { it(this, evento) }
        }
    }

    fun minar() {
        minado = true
    }

    fun reiniciar() {
        aberto = false
        minado = false
        marcado = false
        callbacks.forEach { it(this, CampoEvento.REINICIALIZACAO) }
    }
}
