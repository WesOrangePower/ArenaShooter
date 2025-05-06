package agency.shitcoding.arena.gamestate.tutorial;

import java.util.function.Supplier;

public sealed interface ScriptAtom
    permits ScriptAtom.RunningScriptAtom, ScriptAtom.WaitingScriptAtom, ScriptAtom.ForkingScriptAtom {
  record RunningScriptAtom(Runnable runnable) implements ScriptAtom {}
  record WaitingScriptAtom(Supplier<Boolean> condition) implements ScriptAtom {}
  record ForkingScriptAtom(ScriptBuilder scriptBuilder) implements ScriptAtom {}
}
