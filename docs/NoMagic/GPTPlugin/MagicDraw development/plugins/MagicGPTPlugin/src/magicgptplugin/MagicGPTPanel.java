package magicgptplugin;

import com.nomagic.magicdraw.ui.WindowComponentInfo;
import com.nomagic.magicdraw.ui.browser.WindowComponent;
import com.nomagic.magicdraw.ui.browser.WindowComponentContent;
import com.nomagic.ui.ExtendedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MagicGPTPanel extends ExtendedPanel implements WindowComponent
{
	private final WindowComponentInfo info;
	private final JTextArea chatArea;
	private final JTextArea inputArea;
	private final MagicGPTController controller;
	private final JButton sendButton;

	public MagicGPTPanel(WindowComponentInfo info)
	{
		this.info = info;
		this.controller = new MagicGPTController();
		setLayout(new BorderLayout(8, 8));

		JLabel header = new JLabel("MagicGPT 대화");
		add(header, BorderLayout.NORTH);

		chatArea = new JTextArea(12, 40);
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);
		chatArea.setWrapStyleWord(true);

		JScrollPane chatScroll = new JScrollPane(chatArea);
		add(chatScroll, BorderLayout.CENTER);

		inputArea = new JTextArea(3, 40);
		inputArea.setLineWrap(true);
		inputArea.setWrapStyleWord(true);

		JScrollPane inputScroll = new JScrollPane(inputArea);

		sendButton = new JButton("전송");
		sendButton.addActionListener(this::onSend);

		JButton clearButton = new JButton("지우기");
		clearButton.addActionListener(e -> inputArea.setText(""));

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		buttons.add(clearButton);
		buttons.add(sendButton);

		JPanel inputPanel = new JPanel(new BorderLayout(8, 8));
		inputPanel.add(inputScroll, BorderLayout.CENTER);
		inputPanel.add(buttons, BorderLayout.SOUTH);

		add(inputPanel, BorderLayout.SOUTH);
	}

	@Override
	public WindowComponentInfo getInfo()
	{
		return info;
	}

	@Override
	public WindowComponentContent getContent()
	{
		return new MagicGPTWindowComponentContent(this);
	}

	private void onSend(ActionEvent event)
	{
		String text = inputArea.getText().trim();
		if (text.isEmpty())
		{
			return;
		}

		appendLine("나: " + text);
		inputArea.setText("");
		sendButton.setEnabled(false);
		SwingWorker<String, Void> worker = new SwingWorker<>()
		{
			@Override
			protected String doInBackground()
			{
				return controller.handleQuery(text);
			}

			@Override
			protected void done()
			{
				try
				{
					String response = get();
					appendLine("GPT: " + response);
				}
				catch (Exception error)
				{
					appendLine("GPT: 오류가 발생했습니다 - " + error.getMessage());
				}
				finally
				{
					sendButton.setEnabled(true);
				}
			}
		};
		worker.execute();
	}

	private void appendLine(String line)
	{
		if (chatArea.getDocument().getLength() > 0)
		{
			chatArea.append("\n");
		}
		chatArea.append(line);
		chatArea.setCaretPosition(chatArea.getDocument().getLength());
	}
}
