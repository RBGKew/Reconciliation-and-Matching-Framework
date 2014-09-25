package org.kew.rmf.matchconf.web;
import org.kew.rmf.matchconf.Transformer;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/transformers")
@Controller
@RooWebScaffold(path = "transformers", formBackingObject = Transformer.class)
public class TransformerController {

    void populateEditForm(Model uiModel, Transformer transformer) {
        uiModel.addAttribute("transformer", transformer);
        uiModel.addAttribute("transformers", Transformer.findAllTransformers());
    }
}
