
package devflow.hoseotable;

import android.text.Spanned;

/*
 *  ���� �Խù�(Ʈ��) Ŭ����. 
 */
public class TweetInfo
{
	private String created_at;
	private String from_user;
	private String metadata;
	private Spanned text;
	private boolean sex;

	public boolean isSex() {
		return sex;
	}

	public void setSex(boolean sex) {
		this.sex = sex;
	}

	public String getCreated_at()
	{
		return created_at;
	}

	public void setCreated_at(String created_at)
	{
		this.created_at = created_at;
	}

	public String getFrom_user()
	{
		return from_user;
	}

	public void setFrom_user(String from_user)
	{
		this.from_user = from_user;
	}

	public String getMetadata()
	{
		return metadata;
	}

	public void setMetadata(String metadata)
	{
		this.metadata = metadata;
	}

	public Spanned getText() // Sapnned�� ����� HTML�±׸� �̿밡���ϰ���.
	{
		return text;
	}

	public void setText(Spanned spanned)
	{
		this.text = spanned;
	}

}
