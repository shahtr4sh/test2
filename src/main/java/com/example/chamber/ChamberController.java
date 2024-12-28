package com.example.chamber;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;

@Controller
public class ChamberController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/cast") 
    public String castSpell(@RequestParam("spell") String spell, Model model) {
        // WAF
        if (spell.contains("ProcessBuilder") || 
            spell.contains("getClass") ||
            spell.contains("Runtime") ||
            spell.contains("java") ||
            spell.contains("file") ||
            spell.contains("new") ||
            spell.contains("T(") ||
            spell.contains("#")) {
                return "redirect:/block";
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