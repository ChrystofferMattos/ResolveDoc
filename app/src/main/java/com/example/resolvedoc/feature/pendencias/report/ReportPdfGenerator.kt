package com.example.resolvedoc.feature.pendencias.report

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import com.example.resolvedoc.feature.pendencias.presentation.ReportsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ReportPdfGenerator {

    suspend fun generate(context: Context, state: ReportsUiState) {
        withContext(Dispatchers.IO) {
            val document = PdfDocument()

            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)

            val canvas = page.canvas
            val titlePaint = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
            }
            val textPaint = Paint().apply {
                textSize = 12f
            }
            val highlightPaint = Paint().apply {
                textSize = 14f
                typeface = Typeface.DEFAULT_BOLD
            }

            var y = 40f
            val x = 40f

            canvas.drawText(
                "Relatório de Pendências - RESOLVE DOC CEMAS",
                x,
                y,
                titlePaint
            )
            y += 30f

            canvas.drawText("Resumo Geral", x, y, highlightPaint)
            y += 20f
            canvas.drawText("Total de pendências: ${state.total}", x, y, textPaint); y += 16f
            canvas.drawText("Pendências abertas: ${state.abertas}", x, y, textPaint); y += 16f
            canvas.drawText("Pendências resolvidas: ${state.resolvidas}", x, y, textPaint); y += 16f
            canvas.drawText("Taxa de resolução: ${state.taxaResolucao}%", x, y, textPaint); y += 16f

            val tempoMedio = state.tempoMedioResolucaoDias?.let {
                String.format("%.1f dias", it)
            } ?: "N/D"
            canvas.drawText("Tempo médio de resolução: $tempoMedio", x, y, textPaint); y += 30f

            canvas.drawText("Pendências por Status", x, y, highlightPaint)
            y += 20f
            if (state.porStatus.isEmpty()) {
                canvas.drawText("Nenhum dado disponível.", x, y, textPaint); y += 16f
            } else {
                state.porStatus.forEach { (status, qtd) ->
                    canvas.drawText("- $status: $qtd", x, y, textPaint)
                    y += 16f
                }
            }
            y += 20f

            canvas.drawText("Pendências por Tipo", x, y, highlightPaint)
            y += 20f
            if (state.porTipo.isEmpty()) {
                canvas.drawText("Nenhum dado disponível.", x, y, textPaint); y += 16f
            } else {
                state.porTipo.forEach { (tipo, qtd) ->
                    canvas.drawText("- $tipo: $qtd", x, y, textPaint)
                    y += 16f
                    if (y > 800f) return@forEach
                }
            }

            document.finishPage(page)

            val dir = context.getExternalFilesDir(null) ?: context.filesDir
            val file = File(dir, "relatorio_pendencias.pdf")

            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
            document.close()

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "PDF salvo em: ${file.absolutePath}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
