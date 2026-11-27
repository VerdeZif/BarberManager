
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Ticket - <c:out value="${numeroTicket}"/></title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            padding: 24px;
            background: #f4f0eb;
            color: #231b1c;
            font-family: Arial, sans-serif;
        }

        .ticket {
            width: 100%;
            max-width: 360px;
            margin: 0 auto;
            padding: 24px 18px;
            background: white;
            box-shadow: 0 8px 30px rgba(0, 0, 0, .1);
        }

        .marca {
            text-align: center;
            border-bottom: 1px dashed #888;
            padding-bottom: 16px;
            margin-bottom: 16px;
        }

        .marca h1 {
            font-size: 24px;
            margin: 0;
            letter-spacing: 1px;
            color: #3b0d17;
        }

        .marca p {
            font-size: 12px;
            margin: 6px 0 0;
        }

        .datos {
            font-size: 12px;
            line-height: 1.8;
        }

        .datos strong {
            display: inline-block;
            min-width: 62px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 12px;
            margin-top: 18px;
        }

        th, td {
            padding: 7px 3px;
            text-align: left;
            border-bottom: 1px dashed #bbb;
        }

        .numero {
            text-align: right;
            white-space: nowrap;
        }

        .total {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 15px;
            padding-top: 12px;
            border-top: 2px solid #231b1c;
            font-size: 17px;
            font-weight: bold;
        }

        .seccion {
            margin-top: 20px;
            font-size: 12px;
        }

        .pago {
            display: flex;
            justify-content: space-between;
            margin: 7px 0;
        }

        .nota {
            margin-top: 24px;
            padding-top: 14px;
            border-top: 1px dashed #888;
            text-align: center;
            font-size: 11px;
            line-height: 1.6;
        }

        .acciones {
            max-width: 360px;
            margin: 20px auto;
            display: flex;
            gap: 10px;
        }

        .acciones button,
        .acciones a {
            flex: 1;
            border: 0;
            padding: 12px;
            text-align: center;
            text-decoration: none;
            font-size: 13px;
            cursor: pointer;
            border-radius: 5px;
        }

        .imprimir {
            color: white;
            background: #3b0d17;
        }

        .volver {
            color: #3b0d17;
            background: #e8ded4;
        }

        @media print {
            @page {
                size: 80mm auto;
                margin: 4mm;
            }

            body {
                padding: 0;
                background: white;
            }

            .ticket {
                width: 100%;
                max-width: none;
                padding: 0;
                box-shadow: none;
            }

            .acciones {
                display: none;
            }
        }
    </style>
</head>

<body>

<div class="ticket">

    <header class="marca">
        <h1>BARBERMANAGER</h1>
        <p>Barbería y cuidado personal</p>
        <p>Ticket interno de venta</p>
    </header>

    <div class="datos">
        <div>
            <strong>Ticket:</strong>
            <c:out value="${numeroTicket}"/>
        </div>

        <div>
            <strong>Fecha:</strong>
            <c:out value="${fechaTicket}"/>
        </div>

        <div>
            <strong>Estado:</strong>
            PAGADO
        </div>
    </div>

    <table>
        <thead>
            <tr>
                <th>Descripción</th>
                <th class="numero">Cant.</th>
                <th class="numero">Importe</th>
            </tr>
        </thead>

        <tbody>
            <c:forEach var="item" items="${itemsTicket}">
                <tr>
                    <td>
                        <c:out value="${item.descripcion}"/>
                    </td>

                    <td class="numero">
                        <c:out value="${item.cantidad}"/>
                    </td>

                    <td class="numero">
                        S/
                        <fmt:formatNumber
                            value="${item.subtotal}"
                            minFractionDigits="2"
                            maxFractionDigits="2"/>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <div class="total">
        <span>TOTAL</span>

        <span>
            S/
            <fmt:formatNumber
                value="${totalTicket}"
                minFractionDigits="2"
                maxFractionDigits="2"/>
        </span>
    </div>

    <section class="seccion">
        <strong>Pagos registrados (simulados)</strong>

        <c:forEach var="pago" items="${pagosTicket}">
            <div class="pago">
                <span>
                    <c:out value="${pago.metodo}"/>
                </span>

                <span>
                    S/
                    <fmt:formatNumber
                        value="${pago.monto}"
                        minFractionDigits="2"
                        maxFractionDigits="2"/>
                </span>
            </div>
        </c:forEach>
    </section>

    <footer class="nota">
        <strong>¡Gracias por tu visita!</strong>

        <p>
            Este ticket es un comprobante interno
            generado con fines académicos.
        </p>

        <strong>
            No es un comprobante tributario
            ni un documento autorizado por SUNAT.
        </strong>

        <p>
            Todos los pagos son simulados.
        </p>
    </footer>

</div>

<div class="acciones">
    <button type="button"
            class="imprimir"
            onclick="window.print()">
        Imprimir ticket
    </button>

    <a class="volver"
       href="${empty urlVolverTicket
              ? ctx.concat('/recepcion/ventas')
              : urlVolverTicket}">
        Volver
    </a>

</div>

</body>
</html>