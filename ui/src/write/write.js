import isEqual from 'lodash/isEqual'
import Common from '../common.js'

class Write {

  constructor(element, targetId, draftId) {
    this.$root = $(element)
    this.targetId = targetId
    this.draftId = draftId

    this.$root.find('.btn-submit').tooltip({
      placement: 'top',
      trigger: 'manual'
    })

    this.$root.submit(ev => {
      this.onSubmit(ev)
    })

    this.contentEditor = this.createEditor()

    this.setupEditorSwitch()
    this.setupDraftAutosave()
  }

  onSubmit(ev) {
    ev.preventDefault()

    let data = this.getData()

    if (data.title.length == 0) {
      $('#title-edit').fadeOut().fadeIn()
      Common.popAlert('Title is empty, please input.')
      return
    }

    if (data.content.length == 0) {
      $('#editor-wrapper').fadeOut().fadeIn()
      Common.popAlert('Content is empty, please input.')
      return
    }

    let $form = this.$root
    let $submit = $form.find('.btn-submit')

    let url = $form.attr('action')
    data.draftId = this.draftId
    
    $.post(url, data)
      .done(resp => {
        window.location = resp
      })
      .fail(resp => {
        Common.popAlert('Publish failed because: ' + msg)
        $submit.prop('disabled', false)
      })

    $submit.prop('disabled', true)
  }

  createEditor() {
    if ($('#format').val() == 'MARKDOWN') {
      return this.createMarkdownEditor()
    } else { // HTML
      return this.createRichtextEditor()
    }
  }

  createMarkdownEditor() {
    return {
      simplemde: new SimpleMDE({
        element: $('#content-edit')[0],
        autoDownloadFontAwesome: false,
        spellChecker: false
      }),
      getContent: function () {
        return this.simplemde.value()
      }
    }
  }

  createRichtextEditor() {
    tinymce.init({
      selector: '#content-edit',
      language: 'zh_CN'
    })

    return {
      getContent: function () {
        return tinymce.activeEditor.getContent()
      }
    }
  }

  setupEditorSwitch() {
    let $switchEditorDialog = $('#switch-editor-dialog')

    $switchEditorDialog.on('show.bs.modal', (event) => {
      let $link = $(event.relatedTarget)
      if (this.contentEditor.getContent().length == 0) {
        window.location = $link.data('url')
        event.preventDefault()
      }

      $switchEditorDialog.find('.sure-btn').data('url', $link.data('url'))
    })

    $switchEditorDialog.find('.sure-btn').click((event) => {
      window.location = $(event.target).data('url')
    })
  }

  getData(isFirstTime) {
    return {
      format: $('#format').val(),
      title: $('#title-edit').val(),
      content: isFirstTime ? $('#content-edit').val() : this.contentEditor.getContent()
    }
  }

  setupDraftAutosave() {
    // treat unchanged data as initial draft
    let savedDraft = this.getData(true)

    const autosaveInterval = 3000

    let saveDraft = () => {
      let liveData = this.getData()
      if (isEqual(liveData, savedDraft)) {
        setTimeout(saveDraft, autosaveInterval)
        return
      }

      let payload = {
        draftId: this.draftId,
        targetId: this.targetId,
        format: liveData.format,
        title: liveData.title,
        content: liveData.content,
        format: liveData.format
      }

      $.post("/drafts/save", payload)
        .done(resp => {
          let respDraftId = parseInt(resp)
          if (respDraftId) {
            this.draftId = respDraftId
            savedDraft = liveData
            Common.popAlert('Draft is autosaved', 'info', 1000)
          } else {
            console.info('Draft autosave skipped')
          }
        }).fail(resp => {
          Common.popAlert('Draft autosave failed: ' + Common.toErrorMsg(resp), 'error')
        }).always(() => {
          setTimeout(saveDraft, autosaveInterval)
        })
    }

    setTimeout(saveDraft, autosaveInterval)
  }
}

window.Write = Write
