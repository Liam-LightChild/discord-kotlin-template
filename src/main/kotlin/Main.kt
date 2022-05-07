import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.ChatInputCommandBehavior
import dev.kord.core.behavior.MessageCommandBehavior
import dev.kord.core.behavior.UserCommandBehavior
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.MessageCommandInteractionCreateEvent
import dev.kord.core.event.interaction.UserCommandInteractionCreateEvent
import dev.kord.core.on
import kotlinx.coroutines.flow.toList

data class CommandSet(
    val hello: ChatInputCommandBehavior,
    val greet: UserCommandBehavior
)

/**
 * Main is a suspend function. This allows concurrency using Kotlin co-routines,
 * which Kord absolutely requires.
 */
suspend fun main() {
    val kord = Kord(System.getenv("BOT_TOKEN"))

    val set = createCommands(kord)

    kord.on<ChatInputCommandInteractionCreateEvent> {
        // Received a chat input command (or slash command)
        when (interaction.invokedCommandId) {
            set.hello.id -> interaction.respondPublic { content = "Hello, ${interaction.user.mention}!" }
            else -> interaction.respondEphemeral { content = "Unknown command **/${interaction.invokedCommandName}**" }
        }
    }

    kord.on<UserCommandInteractionCreateEvent> {
        // Received a chat input command (or slash command)
        when (interaction.invokedCommandId) {
            set.greet.id -> interaction.respondPublic { content = "Greetings, ${interaction.target.mention}!" }
            else -> interaction.respondEphemeral { content = "Unknown command **${interaction.invokedCommandName}**" }
        }
    }

    kord.login() // Login to Discord, changing the bots status and listening for events.

    // `login()` above waits eternally, so code down here is never reached.
    //
    // To shut down your bot, force close it. Unless you have some data you need to save,
    // in which case you'll probably want to implement some sort of handler for the stop
    // signals.
}

suspend fun createCommands(kord: Kord): CommandSet {
    val cmds = kord.createGuildApplicationCommands(Snowflake(System.getenv("GUILD_ID").toULong())) {
        input("hello", "Say hi!")
        user("Greet")
    }.toList().associateBy { it.name }

    return CommandSet(
        hello = cmds["hello"] as ChatInputCommandBehavior,
        greet = cmds["Greet"] as UserCommandBehavior
    )
}
