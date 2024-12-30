package com.example.chamber;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import java.util.regex.Pattern;

@Controller
public class ChamberController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/cast") 
 public String castSpell(@RequestParam("spell") String spell, Model model) {
    // Define a regex pattern for valid spell names (e.g., alphanumeric and spaces only)
    String validSpellPattern = "^[a-zA-Z0-9 ]+$";

    // Validate against the pattern
    if (!Pattern.matches(validSpellPattern, spell)) {
        return "redirect:/block";
    }

    // Add spell to the model after validation
    model.addAttribute("spell", spell);
    return "castSpell";
}

        try {
            SpelExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(spell);
            Object result = exp.getValue();
            
            model.addAttribute("result", result);
        } catch (Exception e) {
            model.addAttribute("result", "The spell failed: " + e.getMessage());
        }
        
        return "index";
    }

    @GetMapping("/block")
    public String blockPage() {
        return "block";
    }
}
