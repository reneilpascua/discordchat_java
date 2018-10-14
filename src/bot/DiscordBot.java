package bot;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DiscordBot extends ListenerAdapter {

    boolean echo = false;
    /**
     * log the bot in (given valid token)
     * @param args
     */
    public static void main(String[] args) {
        
        try {
            // jda object is the core of the library. must make jda object that contains out
            // bot token (setToken(String)). creating a jda object w valid token auto logins the bot
            // when the program is run
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken("NTAxMTYxMTcwNTkwMzY3NzQ1.DqVW-w.kAtdV78uyzSQ7WbRYY_UL05INLo")
                    .addEventListener(new DiscordBot()).buildBlocking();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * define responses to MessageReceivedEvent
     */
    public void onMessageReceived(MessageReceivedEvent event) {
        
        MessageChannel channel = event.getChannel(); // returns channel of the event
        String msg = event.getMessage().getContentDisplay(); // returns string representation of message
        User user = event.getAuthor(); // returns user that fired event
        Boolean isbot = user.isBot(); // true if user is a bot
        
        // echo?
        if (echo && !isbot) {
            channel.sendMessage(user.getName() + " said:\n" + msg).queue();
        }
        
        // greet
        if (msg.equalsIgnoreCase("hi") && !isbot) {
            channel.sendMessage("hey there").queue(); // adds message to queue to send
        }
        
        
    }
    
}
