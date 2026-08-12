package behavioural

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

/*Um objeto muda seu comportamento quando seu estado muda.
* Ao invés de escrever vários if ou when, você coloca o comportamento dentro de objetos que representam os estados.
* */

// Ex 1
sealed class AuthorizationState

object Unauthorized : AuthorizationState()

class Authorized(val username: String) : AuthorizationState()

class AuthorizationPresenter {
    private var state: AuthorizationState = Unauthorized

    val isAuthorized: Boolean
        get() = when (state) {
            is Authorized -> true
            is Unauthorized -> false
        }

    val username: String
        get() {
            return when (val state = this.state) {
                is Authorized -> state.username
                is Unauthorized -> "Unknown"
            }
        }

    fun loginUser(username: String) {
        state = Authorized(username)
    }

    fun logoutUser() {
        state = Unauthorized
    }

    override fun toString() = "User $username is logged in: $isAuthorized"
}


// Ex 2

interface PlayerState {
    fun play(player: MusicPlayer)
    fun pause(player: MusicPlayer)
}

class StoppedState : PlayerState {

    override fun play(player: MusicPlayer) {
        println("Iniciando música")
        player.state = PlayingState()
    }

    override fun pause(player: MusicPlayer) {
        println("Já está parado")
    }
}

class PlayingState : PlayerState {

    override fun play(player: MusicPlayer) {
        println("Já está tocando")
    }

    override fun pause(player: MusicPlayer) {
        println("Pausando")
        player.state = PausedState()
    }
}

class PausedState : PlayerState {

    override fun play(player: MusicPlayer) {
        println("Continuando")
        player.state = PlayingState()
    }

    override fun pause(player: MusicPlayer) {
        println("Já está pausado")
    }
}


class MusicPlayer {

    var state: PlayerState = StoppedState()

    fun play() {
        state.play(this)
    }

    fun pause() {
        state.pause(this)
    }
}

class StateTest {
    @Test
    fun testState() {
        val authorizationPresenter = AuthorizationPresenter()

        authorizationPresenter.loginUser("admin")
        println(authorizationPresenter)
        Assertions.assertThat(authorizationPresenter.isAuthorized).isEqualTo(true)
        Assertions.assertThat(authorizationPresenter.username).isEqualTo("admin")

        authorizationPresenter.logoutUser()
        println(authorizationPresenter)
        Assertions.assertThat(authorizationPresenter.isAuthorized).isEqualTo(false)
        Assertions.assertThat(authorizationPresenter.username).isEqualTo("Unknown")
    }
}

class StateTest2 {
    @Test
    fun testState() {
        val player = MusicPlayer()

        player.play()
        player.pause()
        player.play()
    }
}