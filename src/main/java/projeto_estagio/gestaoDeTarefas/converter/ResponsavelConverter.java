package projeto_estagio.gestaoDeTarefas.converter;

import projeto_estagio.gestaoDeTarefas.model.Responsavel;
import projeto_estagio.gestaoDeTarefas.service.ResponsavelService;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("responsavelConverter")
public class ResponsavelConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            Long id = Long.valueOf(value);
            ResponsavelService service = new ResponsavelService();
            return service.buscarResponsavelPorId(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof Responsavel) {
            Responsavel responsavel = (Responsavel) value;
            return responsavel.getId() != null ? responsavel.getId().toString() : "";
        }

        return "";
    }
}
