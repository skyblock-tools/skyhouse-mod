package tools.skyblock.skyhouse.mcmod.commands;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.gui.ConfigGui;

import java.util.Arrays;
import java.util.List;

public class ConfigCommand implements ICommand {

    @Override
    public String getCommandName() {
        return "skyhouse";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("skyhouse", "sh");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        SkyhouseMod.INSTANCE.getConfigManager().processConfig();
        SkyhouseMod.INSTANCE.getListener().openGui(new ConfigGui());
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
