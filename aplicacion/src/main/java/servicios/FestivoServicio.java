package com.festivos.aplicacion.servicios;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.festivos.core.interfaces.IFestivoServicio;
import com.festivos.dominio.entidades.Festivo;
import com.festivos.infraestructura.repositorios.IFestivoRepositorio;

@Service
public class FestivoServicio implements IFestivoServicio {

    private final IFestivoRepositorio repositorio;

    public FestivoServicio(IFestivoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public String esFestivo(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        int año = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH) + 1; // Enero es 0
        int dia = cal.get(Calendar.DAY_OF_MONTH);

        List<Festivo> festivos = repositorio.findAll();
        Date pascua = getDomingoPascua(año);

        for (Festivo f : festivos) {
            Date fechaFestivo = calcularFecha(f, año, pascua);
            Calendar calFestivo = Calendar.getInstance();
            calFestivo.setTime(fechaFestivo);

            // Comparación estricta de mes y día
            if (calFestivo.get(Calendar.MONTH) + 1 == mes && 
                calFestivo.get(Calendar.DAY_OF_MONTH) == dia) {
                return "Es Festivo: " + f.getNombre();
            }
        }
        return "No es festivo";
    }

    private Date calcularFecha(Festivo festivo, int año, Date pascua) {
        Calendar cal = Calendar.getInstance();
        // Limpiamos horas, minutos y segundos para evitar desajustes
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        // Tipo 1: Fijo
        if (festivo.getIdTipo() == 1) {
            cal.set(año, festivo.getMes() - 1, festivo.getDia());
        } 
        // Tipo 2: Ley Emiliani (Se mueve al lunes)
        else if (festivo.getIdTipo() == 2) {
            cal.set(año, festivo.getMes() - 1, festivo.getDia());
            moverAlLunes(cal);
        }
        // Tipo 3: Basado en Pascua
        else if (festivo.getIdTipo() == 3) {
            cal.setTime(pascua);
            if (festivo.getDias_pascua() != null) {
                cal.add(Calendar.DATE, festivo.getDias_pascua());
            }
        }
        // Tipo 4: Basado en Pascua + Ley Emiliani
        else if (festivo.getIdTipo() == 4) {
            cal.setTime(pascua);
            if (festivo.getDias_pascua() != null) {
                cal.add(Calendar.DATE, festivo.getDias_pascua());
            }
            moverAlLunes(cal);
        }
        
        return cal.getTime();
    }

    private void moverAlLunes(Calendar cal) {
        int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
        if (diaSemana != Calendar.MONDAY) {
            // El cálculo correcto para mover al siguiente lunes
            int diasParaSumar;
            if (diaSemana == Calendar.SUNDAY) {
                diasParaSumar = 1;
            } else {
                diasParaSumar = 8 - diaSemana + 1; // Ajuste para llegar al lunes (2)
            }
            cal.add(Calendar.DATE, (diaSemana == Calendar.SUNDAY) ? 1 : (8 - diaSemana + 2) % 7);
            
            // Simplificando la lógica de Emiliani para Colombia:
            // Si no es lunes, mueva al lunes siguiente
            while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                cal.add(Calendar.DATE, 1);
            }
        }
    }

    private Date getDomingoPascua(int año) {
        int a = año % 19;
        int b = año / 100;
        int c = año % 100;
        int d = b / 4;
        int e = b % 4;
        int g = (8 * b + 13) / 25;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 19 * l) / 433;
        int n = (h + l - 7 * m + 90) / 25;
        int p = (h + l - 7 * m + 114) % 31;

        Calendar calendar = Calendar.getInstance();
        calendar.set(año, n - 1, p + 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}