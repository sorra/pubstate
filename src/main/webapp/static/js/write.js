function write_setup () {
  $('.btn-submit').tooltip({
    placement: 'top',
    trigger: 'manual'
  })
  $('#blog').submit(write_formSubmit)

  window.contentEditor = write_createEditor()

  write_setupSwitchEditor()

  write_setupSaveDraft()
}

function write_createEditor() {
  if ($('#contentType').val() == 'markdown') {
    return write_createMarkdownEditor()
  } else {
    return write_createRichtextEditor()
  }
}

function write_createMarkdownEditor() {
  return {
    simplemde: new SimpleMDE({
      element: $('#content')[0],
      autoDownloadFontAwesome: false,
      spellChecker: false
    }),
    getContent: function () {
      return this.simplemde.value()
    }
  }
}

function write_createRichtextEditor() {
  tinymce.init({
    selector: '#content',
    language: 'zh_CN'
  })
  return {
    getContent: function () {
      return tinymce.activeEditor.getContent()
    }
  }
}

function write_formSubmit() {
  try {
    var $form = $(this)
    var selectedTagIds = []
    $form.find('.tag-sel.btn-success').each(function (idx) {
      var tagId = parseInt($(this).attr('tag-id'))
      selectedTagIds.push(tagId)
    })

    var $submit = $form.find('.btn-submit')
    var $title = $('#title')
    if ($title.val().length == 0) {
      $title.fadeOut().fadeIn()
      popAlert('请填写标题')
      return false
    }

    var contentValue = window.contentEditor.getContent()
    $('#content').val(contentValue)
    if (contentValue.length == 0) {
      $('#editor-wrapper').fadeOut().fadeIn()
      popAlert('请填写内容')
      return false
    }

    $submit.prop('disabled', true)
    $form.ajaxSubmit({
      data: {tagIds: selectedTagIds, draftId: window.draftId},
      success: redirect,
      error: function (msg) {
        var $submit = $form.find('.btn-submit')
        popAlert('发表失败: ' + msg)
        $submit.prop('disabled', false)
      }
    })
  } catch (ex) {
    console.error(ex)
  } finally {
    return false
  }
}

function write_setupSwitchEditor() {
  var $switchEditorDialog = $('#switch-editor-dialog')
  $switchEditorDialog.on('show.bs.modal', function (event) {
    var $link = $(event.relatedTarget)
    if (window.contentEditor.getContent().length == 0) {
      redirect($link.data('url'))
      event.preventDefault()
    }
    var $sureBtn = $(this).find('.sure-btn')
    $sureBtn.data('url', $link.data('url'))
  })
  $switchEditorDialog.find('.sure-btn').click(function (event) {
    redirect($(event.target).data('url'))
  })
}

function write_setupSaveDraft() {
  var savedTitle = $('#title').val()
  var savedContent = $('#content').val()

  function saveDraft() {
    var title = $('#title').val()
    var format = $('#format').val()
    var content = window.contentEditor.getContent()
    if (title === savedTitle && content === savedContent) {
      setTimeout(saveDraft, 3000)
      return
    }
    var params = {draftId: draftId, targetId: articleId, title: title, format: format, content: content}
    $.post("/drafts/save", params).done(function (resp) {
      var savedDraftId = parseInt(resp)
      if (savedDraftId) {
        draftId = savedDraftId
      }
      savedTitle = title
      savedContent = content
      popAlert('Draft is saved', 'info', 1000)
    }).fail(function (resp) {
      popAlert('Draft can\'t be saved: ' + errorMsg(resp))
    }).always(function () {
      setTimeout(saveDraft, 3000)
    })
  }

  setTimeout(saveDraft, 3000)
}