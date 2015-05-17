public interface IMessageListener
{
	public boolean awaitingMessage();
	public boolean isDead();
	public void die();
	public void message(String message);
}
